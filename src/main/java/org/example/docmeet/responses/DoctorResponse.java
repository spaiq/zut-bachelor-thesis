package org.example.docmeet.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.docmeet.speciality.Speciality;
import org.example.docmeet.user.User;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Integer id;
    private User user;
    private Speciality speciality;

}
