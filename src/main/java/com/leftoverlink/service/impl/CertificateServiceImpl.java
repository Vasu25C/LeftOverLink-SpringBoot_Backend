package com.leftoverlink.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import com.leftoverlink.model.Food;
import com.leftoverlink.model.FoodAcceptance;
import com.leftoverlink.model.User;
import com.leftoverlink.repository.FoodAcceptanceRepository;
import com.leftoverlink.service.CertificateService;
import com.leftoverlink.service.EmailService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final EmailService emailService;
    private final FoodAcceptanceRepository acceptanceRepo;

    public CertificateServiceImpl(EmailService emailService, FoodAcceptanceRepository acceptanceRepo) {
        this.emailService = emailService;
        this.acceptanceRepo = acceptanceRepo;
    }

    private static final Logger logger = LoggerFactory.getLogger(CertificateServiceImpl.class);
    private final String logoPath = "src/main/resources/static/images/logo.png";
    private final String bgPath = "src/main/resources/static/images/background.jpg";
    private final String signaturePath = "src/main/resources/static/images/signature.png";

    @Override
    public void generateDonorCertificate(FoodAcceptance acceptance) {
        Food food = acceptance.getFood();
        if (!"COMPLETED".equalsIgnoreCase(food.getStatus())) {
            logger.info("❌ Skipped Donor Certificate: Food {} is not COMPLETED", food.getId());
            return;
        }
        if (acceptance.isDonorCertificateSent()) {
            logger.info("✅ Donor Certificate already sent for Acceptance ID {}", acceptance.getId());
            return;
        }

        User donor = food.getDonor();
        User ngo = acceptance.getNgo();
        logger.info("🎯 Generating Donor Certificate: Donor={} NGO={} FoodID={} AcceptanceID={}",
                donor.getEmail(), ngo.getEmail(), food.getId(), acceptance.getId());

        try {
            ByteArrayInputStream pdfStream = createCertificatePdf(
                    "Certificate of Appreciation",
                    donor.getName(),
                    String.format("For donating %d plates of %s to %s on %s.",
                            acceptance.getAcceptedQuantity(),
                            food.getFoodName(),
                            ngo.getName(),
                            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
                    ),
                    "Your contribution as a Donor has helped save surplus food from going to waste " +
                            "and made a real impact in someone's life. We at LeftOverLink are grateful for your commitment to a better world."
            );
            byte[] pdfBytes = pdfStream.readAllBytes();

            emailService.sendCertificate(donor.getEmail(),
                    "🎉 Your Donor Certificate from LeftOverLink",
                    new ByteArrayInputStream(pdfBytes),
                    "donor_certificate.pdf");

            acceptance.setDonorCertificateSent(true);
            acceptanceRepo.save(acceptance);

            logger.info("📤 Donor Certificate sent to {}", donor.getEmail());

        } catch (IOException e) {
            logger.error("❌ Error generating donor certificate for Acceptance ID {}: {}", acceptance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate and send donor certificate", e);
        }
    }

    @Override
    public void generateNgoCertificate(FoodAcceptance acceptance) {
        Food food = acceptance.getFood();
        if (!"COMPLETED".equalsIgnoreCase(food.getStatus())) {
            logger.info("❌ Skipped NGO Certificate: Food {} is not COMPLETED", food.getId());
            return;
        }
        if (acceptance.isNgoCertificateSent()) {
            logger.info("✅ NGO Certificate already sent for Acceptance ID {}", acceptance.getId());
            return;
        }

        User ngo = acceptance.getNgo();
        User donor = food.getDonor();
        logger.info("🎯 Generating NGO Certificate: NGO={} Donor={} FoodID={} AcceptanceID={}",
                ngo.getEmail(), donor.getEmail(), food.getId(), acceptance.getId());

        try {
            ByteArrayInputStream pdfStream = createCertificatePdf(
                    "Certificate of Gratitude",
                    ngo.getName(),
                    String.format("For accepting and distributing %d plates of %s donated by %s on %s.",
                            acceptance.getAcceptedQuantity(),
                            food.getFoodName(),
                            donor.getName(),
                            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
                    ),
                    "Your efforts as an NGO have ensured that excess food reaches those in need. " +
                            "You are a pillar in our mission against food waste. The LeftOverLink team salutes your compassion and action."
            );
            byte[] pdfBytes = pdfStream.readAllBytes();

            emailService.sendCertificate(ngo.getEmail(),
                    "🎉 Your NGO Certificate from LeftOverLink",
                    new ByteArrayInputStream(pdfBytes),
                    "ngo_certificate.pdf");

            acceptance.setNgoCertificateSent(true);
            acceptanceRepo.save(acceptance);

            logger.info("📤 NGO Certificate sent to {}", ngo.getEmail());

        } catch (IOException e) {
            logger.error("❌ Error generating NGO certificate for Acceptance ID {}: {}", acceptance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate and send NGO certificate", e);
        }
    }

    @Override
    public ByteArrayInputStream generateDonorCertificatePdf(User donor, Food food) throws IOException {
        if (!"COMPLETED".equalsIgnoreCase(food.getStatus())) {
            throw new IllegalStateException("Certificate can only be generated after donation is marked COMPLETED.");
        }

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        String detail = String.format("For your generous donation of %d plates of %s to %s on %s.",
                food.getQuantity(),
                food.getFoodName(),
                food.getAcceptedBy() != null ? food.getAcceptedBy().getName() : "an NGO",
                date);

        String message = "Your contribution as a Donor has helped save surplus food from going to waste " +
                "and made a real impact in someone's life. We at LeftOverLink are grateful for your commitment to a better world.";

        return createCertificatePdf("Certificate of Appreciation", donor.getName(), detail, message);
    }

    @Override
    public ByteArrayInputStream generateNGOCertificatePdf(User ngo, Food food, int quantity) throws IOException {
        if (!"COMPLETED".equalsIgnoreCase(food.getStatus())) {
            throw new IllegalStateException("Certificate can only be generated after donation is marked COMPLETED.");
        }

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        String detail = String.format("For accepting and distributing %d plates of %s donated by %s on %s.",
                quantity,
                food.getFoodName(),
                food.getDonor() != null ? food.getDonor().getName() : "a donor",
                date);

        String message = "Your efforts as an NGO have ensured that excess food reaches those in need. " +
                "You are a pillar in our mission against food waste. The LeftOverLink team salutes your compassion and action.";

        return createCertificatePdf("Certificate of Gratitude", ngo.getName(), detail, message);
    }

    @Override
    public ResponseEntity<InputStreamResource> generateDonorCertificate(User donor, Food food) throws IOException {
        return createCertificate(
                "Certificate of Appreciation",
                donor.getName(),
                String.format("For your generous donation of %d plates of %s to %s on %s.",
                        food.getQuantity(),
                        food.getFoodName(),
                        food.getAcceptedBy() != null ? food.getAcceptedBy().getName() : "an NGO",
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
                ),
                "Your contribution as a Donor has helped save surplus food from going to waste and made a real impact in someone's life.",
                "donor_certificate.pdf"
        );
    }

    @Override
    public ResponseEntity<InputStreamResource> generateNGOCertificate(User ngo, Food food, int quantity) throws IOException {
        return createCertificate(
                "Certificate of Gratitude",
                ngo.getName(),
                String.format("For accepting and distributing %d plates of %s donated by %s on %s.",
                        quantity,
                        food.getFoodName(),
                        food.getDonor() != null ? food.getDonor().getName() : "a donor",
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
                ),
                "Your efforts as an NGO have ensured that excess food reaches those in need.",
                "ngo_certificate.pdf"
        );
    }

    // ✅ Certificate with QR code
    private ByteArrayInputStream createCertificatePdf(String title, String personName, String detailText, String message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(100, 100, 100, 100);

        String certificateId = "LOL-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Image bg = new Image(ImageDataFactory.create(bgPath)).scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        bg.setFixedPosition(0, 0).setOpacity(0.15f);
        doc.add(bg);

        Image logo = new Image(ImageDataFactory.create(logoPath)).scaleToFit(80, 80);
        logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        doc.add(logo);

        doc.add(new Paragraph(title).setTextAlignment(TextAlignment.CENTER).setFontSize(24).setBold().setFontColor(ColorConstants.BLUE).setMarginTop(20));
        doc.add(new Paragraph("Awarded to").setTextAlignment(TextAlignment.CENTER).setFontSize(14));
        doc.add(new Paragraph(personName).setTextAlignment(TextAlignment.CENTER).setFontSize(20).setBold().setItalic().setFontColor(ColorConstants.DARK_GRAY));
        doc.add(new Paragraph(detailText).setTextAlignment(TextAlignment.CENTER).setFontSize(14).setMarginTop(20));
        doc.add(new Paragraph(message).setTextAlignment(TextAlignment.CENTER).setFontSize(13).setMarginTop(20));
        doc.add(new Paragraph("Certificate ID: " + certificateId).setTextAlignment(TextAlignment.CENTER).setFontSize(10).setItalic().setMarginTop(10));
        doc.add(new Paragraph("Issued on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).setTextAlignment(TextAlignment.CENTER).setFontSize(12).setMarginTop(5));

        Image signature = new Image(ImageDataFactory.create(signaturePath)).scaleToFit(100, 50);
        signature.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
        doc.add(signature);
        doc.add(new Paragraph("Dr. Nagarathna").setTextAlignment(TextAlignment.CENTER).setFontSize(12).setBold().setItalic());
        doc.add(new Paragraph("Founder, LeftOverLink").setTextAlignment(TextAlignment.CENTER).setFontSize(10));

        try {
            String qrContent = "Certificate ID: " + certificateId + "\nName: " + personName + "\nIssued on: " + LocalDate.now();
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrContent, BarcodeFormat.QR_CODE, 100, 100);
            ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOut);
            Image qrCode = new Image(ImageDataFactory.create(qrOut.toByteArray())).scaleToFit(80, 80);
            qrCode.setFixedPosition(PageSize.A4.getWidth() - 130, 130);
            doc.add(qrCode);
        } catch (WriterException e) {
            logger.warn("⚠️ Failed to generate QR code: {}", e.getMessage());
        }

        doc.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // ✅ Wrapper for ResponseEntity
    private ResponseEntity<InputStreamResource> createCertificate(String title, String name, String detail, String message, String filename) throws IOException {
        ByteArrayInputStream pdf = createCertificatePdf(title, name, detail, message);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}
