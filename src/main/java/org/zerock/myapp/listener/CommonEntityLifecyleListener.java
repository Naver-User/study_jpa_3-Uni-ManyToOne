package org.zerock.myapp.listener;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// --------------------------
// 이 리스너를 통해서, JPA의 모든 엔티티별, 
// 상태전이를 리스닝할 수 있고, 그에 따른 자동 callback 을
// 관리할 수 있는 리스너 클래스입니다.
//--------------------------

// Note: 만일 각 엔티티 클래스에도 같은 상태변이 콜백 메소드가
//       선언되어 있고, 이 리스너 클래스도 함께 적용된 상태라면,
//       엔티티의 상태변이에서 발생하는 이벤트에 대해서, "둘다"
//		 동일한 콜백이 수행되되, 순서는 (1) 리스너 > (2) 각 엔티티
//		 순으로 콜백이 수행됩니다.

@Slf4j
@NoArgsConstructor
public class CommonEntityLifecyleListener {	// POJO로 충분합니다.

	
	@PostLoad
	void postLoad(Object entity) {
		log.trace("1. postLoad({}) invoked.", entity);
	} // postLoad
	
	@PrePersist
	void prePersist(Object entity) {
		log.trace("2. prePersist({}) invoked.", entity);
	} // PrePersist
	
	@PostPersist
	void postPersist(Object entity) {
		log.trace("3. postPersist({}) invoked.", entity);
	} // postLoad
	
	@PreUpdate
	void preUpdate(Object entity) {
		log.trace("4. preUpdate({}) invoked.", entity);
	} // PreUpdate
	
	@PostUpdate
	void postUpdate(Object entity) {
		log.trace("5. postUpdate({}) invoked.", entity);
	} // PostUpdate
	
	@PreRemove
	void preRemove(Object entity) {
		log.trace("6. preRemove({}) invoked.", entity);
	} // PreRemove
	
	@PostRemove
	void postRemove(Object entity) {
		log.trace("7. postRemove({}) invoked.", entity);
	} // postRemove
	
	

} // end class
