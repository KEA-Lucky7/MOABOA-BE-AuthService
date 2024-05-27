package moaboa.auth.global;

import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

}
