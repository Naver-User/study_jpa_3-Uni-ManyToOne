package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecyleListener;

import lombok.Data;


@Data

@EntityListeners(CommonEntityLifecyleListener.class)

//@Entity
@Entity(name = "Member")
@Table(name = "MEMBER")
public class Member implements Serializable {	
	@Serial private static final long serialVersionUID = 1L;

	// 1. 키속성 선언
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;
	
	// 2. 일반속성 선언
	@Basic(optional = false)	// NOT NULL
	private String name;
	   
	// -------------------
	// N(Member) : 1(Team), Uni-direction (Member -> Team)
	// -------------------
   
	@ManyToOne(optional = true, targetEntity = Team.class)
	@JoinColumn(	
		// 아래의 속성정보는 엔티티가 아니라, 테이블 기준으로 설정
		name = "my_team",	// 임의대로 짓는 조인에 부여한 이름
		referencedColumnName = "team_id"	// 진짜 테이블의 컬럼명
	)
	private Team team;	// FK : 이 필드가 바로 외래키를 표현한 필드이다!!! (***)
	
	
	
	
} // end class
