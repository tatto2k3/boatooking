package edu.poly.boatbooking.controller;

import com.google.gson.Gson;
import edu.poly.boatbooking.config.Config;
import edu.poly.boatbooking.dto.PaymentDto;
import edu.poly.boatbooking.dto.PaymentInfoDto;
import edu.poly.boatbooking.entity.Customer;
import edu.poly.boatbooking.entity.Ticket;
import edu.poly.boatbooking.repository.CustomerRepository;
import edu.poly.boatbooking.repository.TicketRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth/payment")
public class PaymentController {

    private CustomerRepository customerRepository;
    private TicketRepository ticketRepository;

    public PaymentController(CustomerRepository customerRepository, TicketRepository ticketRepository) {
        this.customerRepository = customerRepository;
        this.ticketRepository = ticketRepository;
    }

    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> jsonData) throws UnsupportedEncodingException {
        int amount = (int) jsonData.get("ticket_amount");
        String customer_name = (String) jsonData.get("customer_name");
        String customer_email = (String) jsonData.get("customer_email");
        String customer_birth = (String) jsonData.get("customer_birthday");
        String customer_identify = (String) jsonData.get("customer_identify");
        String customer_address = (String) jsonData.get("customer_address");
        String seat_id = (String) jsonData.get("seat_id");
        int schedule = (int) jsonData.get("flight_id");
        String customer_phone = (String) jsonData.get("customer_phone");
        String trip_type = (String) jsonData.get("trip_type");

        String seat_idComeback = trip_type.equals("roundTrip") ? (String) jsonData.get("seat_idComeback") : "";
        int scheduleComeback = trip_type.equals("roundTrip") ? (int) jsonData.get("flight_idComeback") : 0;



        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        amount = amount * 100;
        String bankCode = "NCB";

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");

        PaymentInfoDto.customer_name = customer_name;
        PaymentInfoDto.customer_birth = customer_birth;
        PaymentInfoDto.customer_identify = customer_identify;
        PaymentInfoDto.customer_email = customer_email;
        PaymentInfoDto.customer_address = customer_address;
        PaymentInfoDto.trip_type = trip_type;
        PaymentInfoDto.seat_id = seat_id;
        PaymentInfoDto.seat_idComeback = seat_idComeback;
        PaymentInfoDto.schedule = schedule;
        PaymentInfoDto.scheduleComeback = scheduleComeback;



        String returnUrl = Config.vnp_ReturnUrl;

        vnp_Params.put("vnp_ReturnUrl", returnUrl);

        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);


        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }

            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setStatus("Ok");
        paymentDto.setMessage("Successfully!");
        paymentDto.setURL(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(paymentDto.getURL());
    }

    @GetMapping("/payment_infor")
    public RedirectView transaction()
    {
        String customer_name = PaymentInfoDto.customer_name;
        String customer_birth = PaymentInfoDto.customer_birth;
        String customer_identify = PaymentInfoDto.customer_identify;
        String customer_email = PaymentInfoDto.customer_email;
        String customer_address = PaymentInfoDto.customer_address;
        String trip_type = PaymentInfoDto.trip_type;
        String seat_id = PaymentInfoDto.seat_id;
        int schedule = PaymentInfoDto.schedule;
        String seat_idComeback = PaymentInfoDto.seat_idComeback;
        int scheduleComeback = PaymentInfoDto.scheduleComeback;

        System.out.println("customer_name: " + customer_name);
        System.out.println("customer_email: " + customer_email);
        System.out.println("customer_birth: " + customer_birth);
        System.out.println("customer_identify: " + customer_identify);
        System.out.println("customer_address: " + customer_address);
        System.out.println("seat_id: " + seat_id);
        System.out.println("seat_idComeback: " + seat_idComeback);
        System.out.println("scheduleComeback: " + scheduleComeback);
        System.out.println("schedule: " + schedule);
        System.out.println("trip_type: " + trip_type);

        Customer customer = new Customer();
        customer.setName(customer_name);
        customer.setNumId(customer_identify);
        customer.setEmail(customer_email);
        customer.setBirth(customer_birth);
        customer.setAddress(customer_address);

        customer = customerRepository.save(customer);

        Long c_id = customerRepository.findCustomerIdByNumId(customer_identify).getId();
        long ticket_schedule = Long.parseLong(String.valueOf(schedule));
        Ticket ticket = new Ticket();
        ticket.setSeatId(seat_id);
        ticket.setCId(c_id);
        ticket.setSId(ticket_schedule);
        ticket = ticketRepository.save(ticket);

        if (trip_type.equals("roundTrip")) {
            long ticket_scheduleComeback = Long.parseLong(String.valueOf(scheduleComeback));
            Ticket ticketComeback = new Ticket();
            ticketComeback.setSeatId(seat_idComeback);
            ticketComeback.setCId(c_id);
            ticketComeback.setSId(ticket_scheduleComeback);
            ticketComeback = ticketRepository.save(ticketComeback);
        }

        // Thông tin tài khoản email của bạn
        String senderEmail = "phucnguyen6009dh@gmail.com";
        String password = "ivwnpiguadnfktpa";

        // Thông tin người nhận
        String recipientEmail = customer_email;

        // Cấu hình thông tin máy chủ SMTP
        String host = "smtp.gmail.com";
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Tạo phiên làm việc với máy chủ SMTP
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        try {
            // Tạo đối tượng MimeMessage
            MimeMessage message = new MimeMessage(session);

            // Thiết lập thông tin người gửi và người nhận
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));

            // Thiết lập tiêu đề và nội dung email
            message.setSubject("Ship Ticket Information");
            message.setText("Hello " + customer_name + ",\n\n" +
                    "Thank you for booking your flight with BlueStar Ship. Below are the details of your ship:\n\n" +
                    "From: Phan thiết"  + "\n" +
                    "To: Phú Quý"  + "\n" +
                    "Seat(s): " + seat_id + "\n" +
                    "We look forward to serving you on board.\n\n" +
                    "Safe travels!");
            // Gửi email
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new RedirectView("http://localhost:3000");
    }

}
