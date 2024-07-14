package com.pulse.notification.repository;

import com.pulse.notification.entity.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MemberRepository extends ReactiveCrudRepository<Member, Long> {

}
