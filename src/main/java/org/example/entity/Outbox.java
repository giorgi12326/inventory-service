package org.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.example.dto.Event;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Outbox extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    Event event;

    Integer attempts = 0;

    @Enumerated(EnumType.STRING)
    OutboxStatus status =  OutboxStatus.PENDING;

}
