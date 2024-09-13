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
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecyleListener;

import lombok.Data;


@Data

@EntityListeners(CommonEntityLifecyleListener.class)

@Entity(name = "Team")
@Table(name = "TEAM")
public class Team implements Serializable {
	@Serial private static final long serialVersionUID = 1L;

	// 1. PK 속성 선언
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "team_id")
	private Long id;
	
	
	// 2. 일반속성 선언
	@Basic(optional = false)	// NOT NULL
	private String name;
   
	// -------------------
	// N(Member) : 1(Team), Uni-direction (Member -> Team)
	// -------------------

	
	
} // end class

