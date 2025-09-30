package tn.esprit.spring.entities;

import java.io.Serializable;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@jakarta.persistence.Entity
public class Registration implements Serializable {

	@jakarta.persistence.Id
	@jakarta.persistence.GeneratedValue(strategy= jakarta.persistence.GenerationType.IDENTITY)
	Long numRegistration;
	int numWeek;

	@JsonIgnore
	@jakarta.persistence.ManyToOne
    Skier skier;
	@JsonIgnore
	@jakarta.persistence.ManyToOne
	Course course;
}
