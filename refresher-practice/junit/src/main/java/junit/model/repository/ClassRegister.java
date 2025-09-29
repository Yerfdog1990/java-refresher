package junit.model.repository;

import lombok.Data;

@Data
public class ClassRegister {
    private Integer studentId;
    private Student student;
    private RegistrationStatus registrationStatus;

    public ClassRegister(Integer studentId, Student student, RegistrationStatus registrationStatus) {
        this.studentId = studentId;
        this.student = student;
        this.registrationStatus = registrationStatus;
    }

    @Override
    public String toString() {
        return "ClassRegister{" +
                "studentId=" + studentId +
                ", student=" + student +
                ", registrationStatus=" + registrationStatus +
                '}';
    }
}
