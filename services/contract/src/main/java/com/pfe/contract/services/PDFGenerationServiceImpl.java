package com.pfe.contract.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.pfe.contract.entities.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PDFGenerationServiceImpl implements PDFGenerationService {

    @Override
    public byte[] generateContractPDF(Contract contract) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Configuration de la page
            document.setMargins(50, 50, 50, 50);

            // En-tête
            addHeader(document);
            
            // Titre du contrat
            addContractTitle(document, contract);
            
            // Informations du contrat
            addContractInfo(document, contract);
            
            // Section signatures
            addSignatureSection(document, contract);
            
            // Pied de page
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    @Override
    public byte[] generateSignedContractPDF(Contract contract) {
        return generateContractPDF(contract); // Même logique, mais avec signatures visibles
    }

    private void addHeader(Document document) throws IOException {
        PdfFont titleFont = PdfFontFactory.createFont();
        
        Paragraph header = new Paragraph("VERMEG LIFE INSURANCE")
                .setFont(titleFont)
                .setFontSize(24)
                .setFontColor(ColorConstants.BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        
        document.add(header);
        
        Paragraph subtitle = new Paragraph("Contrat d'assurance")
                .setFont(titleFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
        
        document.add(subtitle);
    }

    private void addContractTitle(Document document, Contract contract) throws IOException {
        PdfFont titleFont = PdfFontFactory.createFont();
        
        Paragraph title = new Paragraph("CONTRAT N° " + contract.getId())
                .setFont(titleFont)
                .setFontSize(18)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        
        document.add(title);
    }

    private void addContractInfo(Document document, Contract contract) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        // Tableau des informations
        Table table = new Table(2).useAllAvailableWidth();
        
        addTableRow(table, "Statut", contract.getStatus() != null ? contract.getStatus() : "ACTIF", font);
        addTableRow(table, "Montant", contract.getMontant() + " DT", font);
        addTableRow(table, "Client ID", contract.getClientId(), font);
        addTableRow(table, "Assureur ID", contract.getInsurerId(), font);
        addTableRow(table, "Bénéficiaire ID", contract.getBeneficiaryId(), font);
        addTableRow(table, "Date de création", formatDate(contract.getCreationDate()), font);
        addTableRow(table, "Date de fin", formatDate(contract.getEndDate()), font);
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addSignatureSection(Document document, Contract contract) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph signatureTitle = new Paragraph("SIGNATURES")
                .setFont(font)
                .setFontSize(14)
                .setFontColor(ColorConstants.BLUE)
                .setMarginTop(30)
                .setMarginBottom(20);
        
        document.add(signatureTitle);
        
        // Tableau des signatures
        Table signatureTable = new Table(2).useAllAvailableWidth();
        
        // Signature client
        String clientSignatureStatus = contract.getClientSignature() != null ? 
            "✓ Signé le " + formatDate(contract.getClientSignedAt()) : "✗ Non signé";
        addTableRow(signatureTable, "Signature Client", clientSignatureStatus, font);
        
        // Signature assureur
        String insurerSignatureStatus = contract.getInsurerSignature() != null ? 
            "✓ Signé le " + formatDate(contract.getInsurerSignedAt()) : "✗ Non signé";
        addTableRow(signatureTable, "Signature Assureur", insurerSignatureStatus, font);
        
        // Statut global
        String globalStatus = Boolean.TRUE.equals(contract.getIsFullySigned()) ? 
            "✓ Contrat entièrement signé" : "✗ En attente de signatures";
        addTableRow(signatureTable, "Statut Global", globalStatus, font);
        
        document.add(signatureTable);
        
        // Ajouter les signatures visuelles si elles existent
        if (contract.getClientSignature() != null) {
            addVisualSignature(document, "Signature Client", contract.getClientSignature());
        }
        
        if (contract.getInsurerSignature() != null) {
            addVisualSignature(document, "Signature Assureur", contract.getInsurerSignature());
        }
    }

    private void addVisualSignature(Document document, String label, String signatureBase64) {
        try {
            // Décoder la signature base64
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            
            // Créer une image à partir des données de signature
            Image signatureImage = new Image(ImageDataFactory.create(signatureBytes));
            signatureImage.setWidth(150);
            signatureImage.setHeight(50);
            signatureImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            
            Paragraph signatureLabel = new Paragraph(label)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            
            document.add(signatureLabel);
            document.add(signatureImage);
            document.add(new Paragraph("\n"));
            
        } catch (Exception e) {
            // Si la signature n'est pas une image valide, afficher un texte
            Paragraph signatureText = new Paragraph(label + ": [Signature électronique]")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(signatureText);
        }
    }

    private void addFooter(Document document) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph footer = new Paragraph("Fait à Tunis, le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(50);
        
        document.add(footer);
        
        Paragraph copyright = new Paragraph("Document généré automatiquement par Vermeg Life Insurance")
                .setFont(font)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
        
        document.add(copyright);
    }

    private void addTableRow(Table table, String label, String value, PdfFont font) {
        table.addCell(new Paragraph(label).setFont(font).setFontSize(12).setBold());
        table.addCell(new Paragraph(value != null ? value : "").setFont(font).setFontSize(12));
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
