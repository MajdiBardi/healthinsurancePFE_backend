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
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.colors.Color;
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

    // Couleurs professionnelles - utilisation des couleurs prédéfinies
    private static final Color PRIMARY_BLUE = ColorConstants.BLUE;
    private static final Color DARK_BLUE = ColorConstants.DARK_GRAY;
    private static final Color LIGHT_GRAY = ColorConstants.LIGHT_GRAY;
    private static final Color MEDIUM_GRAY = ColorConstants.GRAY;
    private static final Color SUCCESS_GREEN = ColorConstants.GREEN;
    private static final Color WARNING_RED = ColorConstants.RED;

    @Override
    public byte[] generateContractPDF(Contract contract) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Configuration de la page avec marges compactes
            document.setMargins(30, 30, 30, 30);

            // En-tête professionnel
            addProfessionalHeader(document);
            
            // Titre du contrat avec style corporate
            addContractTitle(document, contract);
            
            // Informations du contrat avec design moderne
            addContractInfo(document, contract);
            
            // Section signatures avec design premium
            addSignatureSection(document, contract);
            
            // Pied de page professionnel
            addProfessionalFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    @Override
    public byte[] generateSignedContractPDF(Contract contract) {
        return generateContractPDF(contract);
    }

    private void addProfessionalHeader(Document document) throws IOException {
        PdfFont titleFont = PdfFontFactory.createFont();
        
        // En-tête avec fond coloré élégant
        Div headerDiv = new Div()
                .setBackgroundColor(PRIMARY_BLUE)
                .setPadding(10)
                .setMarginBottom(15)
                .setTextAlignment(TextAlignment.CENTER);
        
        Paragraph companyName = new Paragraph("VERMEG LIFE INSURANCE")
                .setFont(titleFont)
                .setFontSize(16)
                .setFontColor(ColorConstants.WHITE)
                .setBold()
                .setMarginBottom(2);
        
        Paragraph tagline = new Paragraph("Contrat d'Assurance Vie")
                .setFont(titleFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.WHITE)
                .setMarginBottom(0);
        
        headerDiv.add(companyName);
        headerDiv.add(tagline);
        document.add(headerDiv);
    }

    private void addContractTitle(Document document, Contract contract) throws IOException {
        PdfFont titleFont = PdfFontFactory.createFont();
        
        // Titre compact
        Paragraph title = new Paragraph("CONTRAT N° " + contract.getId())
                .setFont(titleFont)
                .setFontSize(14)
                .setFontColor(DARK_BLUE)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15);
        
        document.add(title);
    }

    private void addContractInfo(Document document, Contract contract) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        // Titre de section
        Paragraph sectionTitle = new Paragraph("INFORMATIONS DU CONTRAT")
                .setFont(font)
                .setFontSize(12)
                .setFontColor(DARK_BLUE)
                .setBold()
                .setMarginBottom(10);
        document.add(sectionTitle);
        
        // Tableau avec style amélioré
        Table table = new Table(2).useAllAvailableWidth();
        table.setBorder(new SolidBorder(PRIMARY_BLUE, 1));
        
        // En-tête du tableau
        table.addHeaderCell(new Paragraph("CHAMP").setFont(font).setFontSize(10).setBold()
                .setBackgroundColor(PRIMARY_BLUE).setFontColor(ColorConstants.WHITE)
                .setPadding(6).setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(new Paragraph("VALEUR").setFont(font).setFontSize(10).setBold()
                .setBackgroundColor(PRIMARY_BLUE).setFontColor(ColorConstants.WHITE)
                .setPadding(6).setTextAlignment(TextAlignment.CENTER));
        
        // Lignes avec alternance de couleurs
        addStyledTableRow(table, "Statut", getStatusWithIcon(contract.getStatus()), font, true);
        addStyledTableRow(table, "Montant", contract.getMontant() + " DT", font, false);
        addStyledTableRow(table, "Client", contract.getClientId(), font, true);
        addStyledTableRow(table, "Assureur", contract.getInsurerId(), font, false);
        addStyledTableRow(table, "Créé le", formatDate(contract.getCreationDate()), font, true);
        addStyledTableRow(table, "Expire le", formatDate(contract.getEndDate()), font, false);
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addSignatureSection(Document document, Contract contract) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        // Titre compact
        Paragraph signatureTitle = new Paragraph("SIGNATURES")
                .setFont(font)
                .setFontSize(12)
                .setFontColor(DARK_BLUE)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(10);
        
        document.add(signatureTitle);
        
        // Tableau compact des signatures avec images intégrées
        Table signatureTable = new Table(2).useAllAvailableWidth();
        signatureTable.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        
        // Client
        String clientStatus = contract.getClientSignature() != null ? 
            "✅ Signé le " + formatDate(contract.getClientSignedAt()) : "❌ En attente";
        addCompactTableRow(signatureTable, "Client", clientStatus, font);
        
        // Assureur  
        String insurerStatus = contract.getInsurerSignature() != null ? 
            "✅ Signé le " + formatDate(contract.getInsurerSignedAt()) : "❌ En attente";
        addCompactTableRow(signatureTable, "Assureur", insurerStatus, font);
        
        // Statut global
        String globalStatus = Boolean.TRUE.equals(contract.getIsFullySigned()) ? 
            "✅ Contrat entièrement signé" : "⏳ En attente de signatures";
        addCompactTableRow(signatureTable, "Statut", globalStatus, font);
        
        document.add(signatureTable);
        
        // Signatures visuelles compactes
        if (contract.getClientSignature() != null) {
            addVisualSignature(document, "Client", contract.getClientSignature());
        }
        
        if (contract.getInsurerSignature() != null) {
            addVisualSignature(document, "Assureur", contract.getInsurerSignature());
        }
    }

    private void addVisualSignature(Document document, String label, String signatureBase64) {
        try {
            // Nettoyer la signature base64
            String cleanBase64 = signatureBase64;
            if (signatureBase64.contains(",")) {
                cleanBase64 = signatureBase64.substring(signatureBase64.indexOf(",") + 1);
            }
            
            System.out.println("🔍 Tentative de décodage signature pour " + label);
            System.out.println("🔍 Signature originale: " + signatureBase64.substring(0, Math.min(50, signatureBase64.length())) + "...");
            System.out.println("🔍 Signature nettoyée: " + cleanBase64.substring(0, Math.min(50, cleanBase64.length())) + "...");
            
            // Décoder la signature base64
            byte[] signatureBytes = Base64.getDecoder().decode(cleanBase64);
            System.out.println("✅ Signature décodée avec succès, taille: " + signatureBytes.length + " bytes");
            
            // Créer l'image avec gestion d'erreur améliorée
            com.itextpdf.io.image.ImageData imageData;
            try {
                imageData = ImageDataFactory.create(signatureBytes);
                System.out.println("✅ ImageData créé - Largeur: " + imageData.getWidth() + ", Hauteur: " + imageData.getHeight());
            } catch (Exception imgException) {
                System.err.println("❌ Erreur création ImageData: " + imgException.getMessage());
                throw imgException;
            }
            
            // Créer l'image avec dimensions fixes pour éviter les blocs noirs
            Image signatureImage = new Image(imageData);
            
            // Forcer des dimensions spécifiques - plus petites pour tenir sur 1 page
            signatureImage.setWidth(100);
            signatureImage.setHeight(30);
            signatureImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            
            // Signature compacte sans cadre pour économiser l'espace
            Paragraph signatureLabel = new Paragraph(label)
                    .setFontSize(8)
                    .setFontColor(DARK_BLUE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(3);
            
            document.add(signatureLabel);
            document.add(signatureImage);
            
            System.out.println("✅ Signature " + label + " ajoutée au PDF avec succès");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ajout de la signature " + label + ": " + e.getMessage());
            e.printStackTrace();
            
            // Signature de remplacement simple
            Paragraph signatureText = new Paragraph("✍️ " + label + " - Signature électronique")
                    .setFontSize(8)
                    .setFontColor(SUCCESS_GREEN)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(signatureText);
        }
    }

    private void addProfessionalFooter(Document document) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        // Footer compact
        Paragraph footer = new Paragraph("Fait à Tunis, le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFont(font)
                .setFontSize(9)
                .setFontColor(MEDIUM_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        
        document.add(footer);
    }

    private void addCompactTableRow(Table table, String label, String value, PdfFont font) {
        table.addCell(new Paragraph(label).setFont(font).setFontSize(10).setBold()
                .setPadding(5));
        
        table.addCell(new Paragraph(value != null ? value : "").setFont(font).setFontSize(10)
                .setPadding(5));
    }

    private void addStyledTableRow(Table table, String label, String value, PdfFont font, boolean isEven) {
        Color backgroundColor = isEven ? LIGHT_GRAY : ColorConstants.WHITE;
        
        table.addCell(new Paragraph(label).setFont(font).setFontSize(9).setBold()
                .setBackgroundColor(backgroundColor).setPadding(4)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        
        table.addCell(new Paragraph(value != null ? value : "").setFont(font).setFontSize(9)
                .setBackgroundColor(backgroundColor).setPadding(4)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
    }

    private String getStatusWithIcon(String status) {
        if (status == null) return "🟢 ACTIF";
        switch (status.toUpperCase()) {
            case "ACTIF":
                return "🟢 ACTIF";
            case "SUSPENDU":
                return "🟡 SUSPENDU";
            case "EXPIRÉ":
                return "🔴 EXPIRÉ";
            default:
                return "🟢 " + status;
        }
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}