package edu.poly.boatbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PaymentInfoDto {
    public static String customer_name = "";
    public static String customer_birth = "";
    public static String customer_identify = "";
    public static String customer_email = "";
    public static String customer_address = "";
    public static String trip_type = "";
    public static String seat_id = "";
    public static int schedule = 0;
    public static String seat_idComeback = "";
    public static int scheduleComeback = 0;

}
