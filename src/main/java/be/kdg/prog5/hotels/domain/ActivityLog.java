package be.kdg.prog5.hotels.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ActivityLog {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActivityType action;

    private String description;
    private LocalDateTime timestamp;

    @ManyToOne
    private ApplicationUser user;

    // Required by JPA
    public ActivityLog() {
    }

    // Constructor used in service
    public ActivityLog(ActivityType action, String description, LocalDateTime timestamp, ApplicationUser user) {
        this.action = action;
        this.description = description;
        this.timestamp = timestamp;
        this.user = user;
    }

    // getters
    public Long getId() {
        return id;
    }

    public ActivityType getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public ApplicationUser getUser() {
        return user;
    }
}