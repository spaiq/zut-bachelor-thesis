package org.example.docmeet.utils;

import org.example.docmeet.doctor.Doctor;
import org.example.docmeet.user.User;

import java.util.stream.Stream;

public class Utils {

    public static String getFullName(User user, Doctor doctor) {
        return Stream.of(doctor.getTitle(), user.getName(), user.getSecondName(), user.getSurname())
                     .filter(str -> str != null && !str.isBlank())
                     .reduce((str1, str2) -> str1 + " " + str2)
                     .orElse("");
    }

}
