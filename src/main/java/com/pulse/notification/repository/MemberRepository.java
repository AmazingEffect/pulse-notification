package com.pulse.notification.repository;

import com.pulse.notification.domain.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {

}
