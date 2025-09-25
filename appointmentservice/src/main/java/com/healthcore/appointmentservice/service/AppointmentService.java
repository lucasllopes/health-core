// ...existing code...
import com.healthcore.appointmentservice.dto.UpdateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.AppointmentResponseDTO;
import com.healthcore.appointmentservice.dto.AppointmentNotificationMessageDTO;
import com.healthcore.appointmentservice.producer.NotificationMessageSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
// ...existing code...
    private final NotificationMessageSender notificationMessageSender;
// ...existing code...
    public AppointmentService(AppointmentRepository appointmentRepository,
                              AppointmentProducerService appointmentProducerService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              NurseRepository nurseRepository,
                              NotificationMessageSender notificationMessageSender) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentProducerService = appointmentProducerService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.notificationMessageSender = notificationMessageSender;
    }
// ...existing code...
    public AppointmentResponseDTO create(CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        Appointment appointment = buildAppointment(createAppointmentRequestDTO);
        appointment.setStatus(createAppointmentRequestDTO.status());
        appointmentRepository.save(appointment);
        AppointmentNotificationDTO event = buildAppointmentNotification(appointment);
        appointmentProducerService.sendAppointmentCreated(event);
        // Envio para fila de notificação
        AppointmentNotificationMessageDTO notificationMsg = toNotificationMessageDTO(appointment);
        notificationMessageSender.sendNotification(notificationMsg);
        return toResponseDTO(appointment);
    }

    public Page<AppointmentResponseDTO> getAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public Optional<AppointmentResponseDTO> getById(Long id) {
        return appointmentRepository.findById(id).map(this::toResponseDTO);
    }

    public AppointmentResponseDTO update(Long appointmentId, UpdateAppointmentRequestDTO updateRequest) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (updateRequest.getPatientId() != null) {
            existingAppointment.setPatient(findPatientById(updateRequest.getPatientId()));
        }
        if (updateRequest.getDoctorId() != null) {
            existingAppointment.setDoctor(findDoctorById(updateRequest.getDoctorId()));
        }
        if (updateRequest.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(updateRequest.getAppointmentDate());
        }
        if (updateRequest.getStatus() != null) {
            existingAppointment.setStatus(updateRequest.getStatus());
        }
        if (updateRequest.getNotes() != null) {
            existingAppointment.setNotes(updateRequest.getNotes());
        }
        existingAppointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(existingAppointment);
        // Envio para fila de notificação
        AppointmentNotificationMessageDTO notificationMsg = toNotificationMessageDTO(existingAppointment);
        notificationMessageSender.sendNotification(notificationMsg);
        return toResponseDTO(existingAppointment);
    }

    public void delete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));
        appointmentRepository.delete(appointment);
    }

    private AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setDoctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null);
        dto.setPatientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null);
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setNotes(appointment.getNotes());
        dto.setStatus(appointment.getStatus() != null ? appointment.getStatus().name() : null);
        return dto;
    }

    private AppointmentNotificationMessageDTO toNotificationMessageDTO(Appointment appointment) {
        AppointmentNotificationMessageDTO dto = new AppointmentNotificationMessageDTO();
        dto.setAppointmentId(appointment.getId());
        dto.setPatientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null);
        dto.setDoctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null);
        dto.setNurseId(appointment.getNurse() != null ? appointment.getNurse().getId() : null);
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
// ...existing code...
