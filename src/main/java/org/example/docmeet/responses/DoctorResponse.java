package org.example.docmeet.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.docmeet.speciality.Speciality;
import org.example.docmeet.user.User;

@Builder
@Data
@AllArgsConstructor
public class DoctorResponse {
    private Integer id;
    private User user;
    private Speciality speciality;

}
