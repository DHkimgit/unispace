//package com.project.unispace.domain.university.repository;
//
//import com.project.unispace.domain.university.entity.College;
//import com.project.unispace.domain.university.entity.University;
//import com.project.unispace.domain.university.entity.UniversityDepartment;
//import jakarta.persistence.*;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//import java.util.function.Consumer;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//public class UniversityTest {
//    @Autowired
//    private UniversityRepository universityRepository;
//
//    @Autowired
//    private CollegeRepository collegeRepository;
//
//    @PersistenceUnit
//    private EntityManagerFactory emf;
//
//    private University university;
//    private College college;
//    private UniversityDepartment universityDepartment;
//
//    @BeforeEach
//    void setUp() {
//        university = University.createUniversity("서울대학교", "관악구");
//        college = College.createCollege(university, "공과대학");
//        universityDepartment = UniversityDepartment.createUniversityDepartment("컴퓨터공학부", false, university);
//    }
//
//    @Test
//    @DisplayName("대학에 단과대학을 추가한다")
//    void addCollegeToUniversity(){
//        university.addCollege(college);
//
//        Assertions.assertThat(university.getColleges().getFirst()).isEqualTo(college);
//        Assertions.assertThat(college.getUniversity()).isEqualTo(university);
//    }
//
//    @Test
//    @DisplayName("대학에 단과대학에 속하지 않는 독립 학과를 추가한다")
//    void addUniversityDepartmentToUniversity(){
//        university.addUniversityDepartment(universityDepartment);
//
//        Assertions.assertThat(university.getUniversityDepartments().getFirst()).isEqualTo(universityDepartment);
//        Assertions.assertThat(universityDepartment.getUniversity()).isEqualTo(university);
//    }
//
//    @Test
//    @DisplayName("대학에 속한 단과대학을 제거한다")
//    void deleteCollegeFromUniversity(){
//        university.addCollege(college);
//        universityRepository.save(university);
//        Optional<University> findUniversity = universityRepository.findById(university.getId());
//        Assertions.assertThat(findUniversity.get().getColleges().getFirst().getName()).isEqualTo("공과대학");
//
//        Optional<College> findCollege = collegeRepository.findById(college.getId());
//        Assertions.assertThat(findCollege.get().getName()).isEqualTo("공과대학");
//
//        findUniversity.get().removeCollege(college);
//        universityRepository.save(findUniversity.get());
//
//        Optional<University> updatedFindUniversity = universityRepository.findById(university.getId());
//        Assertions.assertThat(updatedFindUniversity.get().getColleges()).isEmpty();
//        Optional<College> findUpdatedCollege = collegeRepository.findById(college.getId());
//        Assertions.assertThat(findUpdatedCollege).isPresent();
//    }
//
//    @Test
//    @DisplayName("대학 데이터 저장 테스트 - 데이터베이스에 직접 접근")
//    void 대학데이터저장테스트스프링데이터JPA없이(){
//        transaction(em -> {
//            university.addCollege(college);
//            university.addUniversityDepartment(universityDepartment);
//            em.persist(university);
//            em.flush();
//            em.clear();
//            University findUniversity = em.find(University.class, university.getId());
//            Assertions.assertThat(findUniversity).isNotNull();
//            Assertions.assertThat(findUniversity.getColleges().getFirst().getName()).isEqualTo("공과대학");
//            Assertions.assertThat(findUniversity.getUniversityDepartments().getFirst().getName()).isEqualTo("컴퓨터공학부");
//        });
//    }
//
//    @Test
//    @DisplayName("종속 학과, 단과대학을 포함한 대학 데이터 저장 테스트")
//    void 대학데이터저장테스트(){
//        //===연관관계 편의 메서드 테스트===//
//        university.addCollege(college);
//        university.addUniversityDepartment(universityDepartment);
//
//        universityRepository.save(university);
//        universityRepository.flush();
//
//        Optional<University> findUniversity = universityRepository.findById(university.getId());
//
//        Assertions.assertThat(findUniversity).isPresent();
//        Assertions.assertThat(findUniversity.get()).isEqualTo(university);
//        Assertions.assertThat(findUniversity.get().getColleges().getFirst()).isEqualTo(college);
//        Assertions.assertThat(findUniversity.get().getUniversityDepartments().getFirst()).isEqualTo(universityDepartment);
//
//    }
//
//    @Test
//    @DisplayName("종속 학과, 단과대학이 없는 대학 데이터 저장 테스트")
//    void 대학단일데이터저장테스트(){
//        universityRepository.save(university);
//
//        Optional<University> findUniversity = universityRepository.findById(university.getId());
//
//        Assertions.assertThat(findUniversity).isPresent();
//        Assertions.assertThat(findUniversity.get()).isEqualTo(university);
//    }
//
//
//
//    @Test
//    @DisplayName("저장 테스트")
//    void saveTest() {
//        // 대학 저장
//        universityRepository.save(university);
//        assertNotNull(university.getId());
//
//        // 대학에 속할 학부 저장
//        university.addCollege(college);
//        universityRepository.save(university);
//
//        // 대학에 속할 학과 저장
//        university.addUniversityDepartment(universityDepartment);
//        universityRepository.save(university);
//
//        // DB에 저장된 데이터 조회
//        University foundUniversity = universityRepository.findById(university.getId()).orElse(null);
//        assertNotNull(foundUniversity);
//        assertEquals(1, foundUniversity.getColleges().size());
//        assertEquals(1, foundUniversity.getUniversityDepartments().size());
//        assertEquals("서울대", foundUniversity.getName());
//
//    }
//
//    void transaction(Consumer<EntityManager> consumer) {
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();
//        try {
//            consumer.accept(em);
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            tx.rollback();
//            em.close();
//        }
//    }
//}
