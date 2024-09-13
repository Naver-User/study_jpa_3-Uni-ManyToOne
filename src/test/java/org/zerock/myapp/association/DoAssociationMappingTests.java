package org.zerock.myapp.association;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.zerock.myapp.entity.Member;
import org.zerock.myapp.entity.Team;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@NoArgsConstructor

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DoAssociationMappingTests {
	private EntityManagerFactory emf;
	private EntityManager em;
	
	
	
	@BeforeAll
	void beforeAll() {	// 1회성 전처리 : 팩토리 생성
		log.trace("beforeAll() invoked.");
		
		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.H2);
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.ORACLE);
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.MYSQL);
		
		assertNotNull(this.emf);
		log.info("\t+ this.emf: {}", this.emf);
		
		// ------------
		this.em = this.emf.createEntityManager();
		assert this.em != null;
		log.info("\t+ this.em: {}", this.em);
	} // beforeAll
	
	@AfterAll
	void afterAll() {	// 1회성 후처리: 팩토리 파괴
		log.trace("afterAll() invoked.");
				
		this.em.clear();	// For VM GC
		
		try { this.em.close(); } catch(Exception _ignored) {} 
		try { this.emf.close(); } catch(Exception _ignored) {} 		
	} // afterAll
	
	
	
//	@Disabled
	@Order(1)
//	@Test
	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value=1L, unit=TimeUnit.MINUTES)
	void prepareData() {
		log.trace("prepareData() invoked.");
		
		try {
			this.em.getTransaction().begin();

			// -- 1 ---------------------
			LongStream.of(1L, 2L, 3L).forEachOrdered(seq -> {
				Team transientTeam = new Team();
				
				// 키속성의 값이 @GeneratedValue 어노테이션에 의해서 자동생성되게 되어 있음에도 불구하고
				// 직접 아래와 같이, 키속성에 값을 설정(=결정)하면, 무조건 오류가 발생 => 오류의 의미가
				// Detached 된 엔티티는 persist(save) 할수가 없습니다 란 의미의 오류가 발생
//				transientTeam.setId(seq);			// XX, 실험 - 위와같이 오류발생
				transientTeam.setName("TEAM-"+seq);
				
				this.em.persist(transientTeam);
			}); // forEachOrdered
			
			// -- 2 ---------------------
			Team team1 = em.<Team>find(Team.class, 1L);
			Team team2 = em.<Team>find(Team.class, 2L);
			Team team3 = em.<Team>find(Team.class, 3L);
			
			Objects.requireNonNull(team1);
			assertNotNull(team2);
			assert team3 != null;

			// -- 3 ---------------------
			IntStream.rangeClosed(1, 6).forEachOrdered(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME-"+seq);
				transientMember.setTeam(team1);
				
				this.em.persist(transientMember);	// TRANSIENT(NEW) -> MANAGED
			});	// .forEachOrdered

			// -- 4 ---------------------
			IntStream.of(7, 8, 9).forEachOrdered(seq -> {
				Member transientMember = new Member();
				transientMember.setName("NAME-"+seq);
				
				if(seq != 9) {				
					transientMember.setTeam(team3);
				} // if
				
				this.em.persist(transientMember);
			});	// .forEachOrdered 
			
			this.em.getTransaction().commit();
		} catch(Exception e) {
			this.em.getTransaction().rollback();
			throw e;
		} // try-catch
	} // prepareData
	
	
	
//	@Disabled
	@Order(2)
//	@Test
	@RepeatedTest(1)
	@DisplayName("2. testFindBelongedToTheTeam")
	@Timeout(value=1L, unit=TimeUnit.MINUTES)
	void testFindBelongedToTheTeam() {
		log.trace("testFindBelongedToTheTeam() invoked.");
		
		// -- 1 ---------------------
		// For the specified memer.
		final Long id = 7L;
		Member foundMember7 = this.em.<Member>find(Member.class, id);
		
		Objects.requireNonNull(foundMember7);
		log.info("\t+ foundMember7: {}, isContains: {}", 
				foundMember7, this.em.contains(foundMember7));
		
		Team myTeam = foundMember7.getTeam();
		assertNotNull(myTeam);
		log.info("\t+ myTeam: {}", myTeam);

		// -- 2 ---------------------
		// For all members.
		
		IntStream.rangeClosed(1, 9).forEachOrdered(seq -> {
//			Member foundMember = this.em.<Member>find(Member.class, seq);	// OK
			Member foundMember = this.em.<Member>find(Member.class, Long.valueOf(seq));	// OK
			
			assertNotNull(foundMember);
			log.info("\t+ team: {}", foundMember.getTeam());
		});	// .forEachOrdered
		
	} // testFindBelongedToTheTeam
	
		
//	@Disabled
	@Order(3)
//	@Test
	@RepeatedTest(1)
	@DisplayName("3. testFindAllMembersWithJPQL")
	@Timeout(value=1L, unit=TimeUnit.MINUTES)
	void testFindAllMembersWithJPQL() {
		log.trace("testFindAllMembersWithJPQL() invoked.");
		
		// N:1 단방향 관계를 무시하고, 반대로 ...
		// JPQL을 이용하여, 자식테이블의 FK컬럼을 이용하여,
		// 특정팀에 속한 모든 팀원을 조회하자! 
		// 이를 통해, 관계형 데이터 모델을 따르는 데이터베이스는
		// FK를 통해, "양방향" 조회가 가능함을 경험하자!
		
		// 중요사항: JPQL의 문법은 Native SQL과 거의 대동소이합니다.
		//           하지만, 이 문장에서 기술되는 테이블명은 -> "엔티티명"이고
		//           이 문장에서 기술되는 컬럼명은 -> "엔티티의 속성명" 임을 주의하세요
		// JPQL은 엔티티를 대상으로 조회하는 것입니다!! (****************)

		// -- 1 ---------------------
		String jpql = "SELECT m FROM Member m WHERE m.team.id = 3 ORDER BY m.id DESC";
		
		TypedQuery<Member> typedQuery = 
				this.em.<Member>createQuery(jpql, Member.class);
		
		assertNotNull(typedQuery);
		log.info("\t+ typedQuery: {}", typedQuery);

		// -- 2 ---------------------
		List<Member> resultList = typedQuery.getResultList();
		resultList.forEach(m -> {
			log.info("\t+ member: id({}), team id({})", 
					m.getId(), m.getTeam().getId());
		});	// .forEach		
	} // testFindAllMembersWithJPQL
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Disabled
	@Order(4)
//	@Test
	@RepeatedTest(1)
	@DisplayName("4. testFindAllMembersWithNativeSQL")
	@Timeout(value=1L, unit=TimeUnit.MINUTES)
	void testFindAllTeamsWithNativeSQL() {
		log.trace("testFindAllMembersWithNativeSQL() invoked.");
		
		// N:1 단방향 관계를 무시하고, 반대로 ...
		// Native SQL을 이용하여, 자식테이블의 FK컬럼을 이용하여,
		// 특정팀에 속한 모든 팀원을 조회하자! 
		// 이를 통해, 관계형 데이터 모델을 따르는 데이터베이스는
		// FK를 통해, "양방향" 조회가 가능함을 경험하자!

		// -- 1 ---------------------
		String sql = "SELECT * FROM member WHERE my_team = 1 ORDER BY member_id DESC";
		Query nativeQuery = this.em.createNativeQuery(sql, Member.class);
		log.info("\t+ nativeQuery: {}", nativeQuery);

		// -- 2 ---------------------
		List resultList = nativeQuery.getResultList();
		resultList.forEach(obj -> {
			if(obj instanceof Member m) {
				log.info("\t+ member: id({}), team id({})", 
						m.getId(), m.getTeam().getId());
			} // if
		}); // .forEach
	} // testFindAllMembersWithNativeSQL
	
	
	
} // end class


