package com.muzaffar.studentattendancecontrol.repository.jpa;

import com.muzaffar.studentattendancecontrol.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}
