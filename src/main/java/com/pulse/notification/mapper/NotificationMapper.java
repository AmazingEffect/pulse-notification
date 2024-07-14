package com.pulse.notification.mapper;

import com.pulse.notification.controller.http.response.NotificationDTO;
import com.pulse.notification.domain.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    NotificationDTO toDto(Notification notification);

}
