package tn.esprit.spring.testutil;

import tn.esprit.spring.entities.*;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * Utility class for creating test data objects
 * This helps maintain consistency across different test classes
 */
public class TestDataBuilder {

    public static Skier createTestSkier(String firstName, String lastName, String city) {
        Skier skier = new Skier();
        skier.setFirstName(firstName);
        skier.setLastName(lastName);
        skier.setDateOfBirth(LocalDate.of(1990, 5, 15));
        skier.setCity(city);
        skier.setRegistrations(new HashSet<>());
        return skier;
    }

    public static Subscription createTestSubscription(TypeSubscription type, float price) {
        Subscription subscription = new Subscription();
        subscription.setTypeSub(type);
        subscription.setStartDate(LocalDate.now());
        subscription.setPrice(price);
        
        // Set end date based on subscription type
        switch (type) {
            case ANNUAL:
                subscription.setEndDate(LocalDate.now().plusYears(1));
                break;
            case SEMESTRIEL:
                subscription.setEndDate(LocalDate.now().plusMonths(6));
                break;
            case MONTHLY:
                subscription.setEndDate(LocalDate.now().plusMonths(1));
                break;
        }
        
        return subscription;
    }

    public static Course createTestCourse(TypeCourse typeCourse, Support support, int level, float price) {
        Course course = new Course();
        course.setTypeCourse(typeCourse);
        course.setSupport(support);
        course.setLevel(level);
        course.setPrice(price);
        course.setTimeSlot(6);
        return course;
    }

    public static Piste createTestPiste(String name, Color color, int length, int slope) {
        Piste piste = new Piste();
        piste.setNamePiste(name);
        piste.setColor(color);
        piste.setLength(length);
        piste.setSlope(slope);
        return piste;
    }

    public static Instructor createTestInstructor(String firstName, String lastName, LocalDate dateOfHire) {
        Instructor instructor = new Instructor();
        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
        instructor.setDateOfHire(dateOfHire);
        return instructor;
    }
}