package com.Timbua.backend.config;

import com.Timbua.backend.model.MaterialSite;
import com.Timbua.backend.service.MaterialSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class MaterialSiteDataLoader implements CommandLineRunner {

    @Autowired
    private MaterialSiteService materialSiteService;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists to avoid duplicates
        if (materialSiteService.getAllMaterialSites().isEmpty()) {
            // Load all 34 records from your Excel file
            List<MaterialSite> initialSites = Arrays.asList(
                    // Row 1
                    new MaterialSite(1, "001B", "Sand", "Junda-Kilalapo kwa kishanga",
                            new BigDecimal("-4.01597"), new BigDecimal("39.672954"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Foundations, walling, paving",
                            "21-50 men. 1-10 women", "Ngomeni, Bengala, Timboni", "1-10 tonnes", null),

                    // Row 2
                    new MaterialSite(2, "001B", "Bricks", "Junda- Misleni",
                            new BigDecimal("-4.010551"), new BigDecimal("39.672492"), "in the county",
                            "Small industry", "1 year", "Individual", "Walling",
                            "1-10 men", "Kanjiweni", "400 tons", null),

                    // Row 3
                    new MaterialSite(3, "001B", "Ventilation blocks", "Junda- Misleni",
                            new BigDecimal("-4.010551"), new BigDecimal("39.672492"), "in the county",
                            "Small industry", "2 year", "Individual", "Decoration , interior design",
                            "1-10 men", null, "1-10 units", null),

                    // Row 4
                    new MaterialSite(4, "001B", "Limestone", "Junda_Kilalapo kwa kishanga",
                            new BigDecimal("-4.0145971"), new BigDecimal("39.672954"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Foundations",
                            "21-50 men", null, "1-10 units", null),

                    // Row 5
                    new MaterialSite(5, "001B", "Tmber/wood", "Mwakurunge- Usederi",
                            new BigDecimal("-3.9842"), new BigDecimal("39.693988"), "in the county",
                            "Small industry", "1 year", "Individual", "Roofing, interior design",
                            "1-10 men", null, "100 - 500 units", "The timber is used for doors and roofing"),

                    // Row 6
                    new MaterialSite(6, "001B", "Blocks", "Mwakurunge- Ngutatu",
                            new BigDecimal("-3.989252"), new BigDecimal("39.68099"), "in the county",
                            "Small industry", "2 year", "Individual", "Foundations, walling , roofing",
                            "1-10 men", null, "100 - 500 units", "There are several men producing similar blocks in the area"),

                    // Row 7
                    new MaterialSite(7, "001B", "Stones that make cement", "Mwakurunge- Ugatunzi(Kiembeni)",
                            new BigDecimal("-3.984019"), new BigDecimal("39.693979"), "in the county",
                            "Large industry, quarrying", "10-19 years", "Shale quarry(private company)", "Foundaations, walling , paving, decoration, interior design , flooring",
                            "1-10 men", null, "500-1000 units", "The product is transported to bamburi cement for manufacture of cement"),

                    // Row 8
                    new MaterialSite(8, "001B", "Makuti", "Bamburi_wema",
                            new BigDecimal("-3.992309"), new BigDecimal("39.7048115"), "in the county",
                            "Smal industry", "5-9 years", "Kijiji farm(individual)", "Roofing",
                            "1-10 men", "Around Wema", "150-500", "Many individuals make makuti"),

                    // Row 9
                    new MaterialSite(9, "001B", "Ventilation blocks", "Bamburi-Balawa",
                            new BigDecimal("-3.9833"), new BigDecimal("39.714746"), "in the county",
                            "Smal industry", "5-9 years", "Individual", "Decoration , interior design",
                            "1-10 men", null, "500 - 1000 units", null),

                    // Row 10
                    new MaterialSite(10, "001B", "Concrete(used to make cemet)", "Bamburi- Timbo(Kashani)",
                            new BigDecimal("-3.967072"), new BigDecimal("39.708678"), "in the county",
                            "Quarrying", "5-9 years", "Individual", "Foundation, Paving, Flooring",
                            "1-10 men", "Zimlati", "501 - 1000 tonnes", "Machines do most of the work, Many places that provide concrete are around Kashana area"),

                    // Row 11
                    new MaterialSite(11, "001B", "Sand", "Bamburi_ Kashani",
                            new BigDecimal("-3.967072"), new BigDecimal("39.708678"), "in the county",
                            "Quarrying", "5-9 years", "Individual", "Foundation, walling, paving, interior design, maintenance, flooring",
                            "1-10 men", "Around kashani area, Zimlati", "502 - 1000 tonnes", "Machines do most of the work, Many places that provide concrete are around Kashana area"),

                    // Row 12
                    new MaterialSite(12, "001B", "Bulding stones", "Bamburi_ Kashani",
                            new BigDecimal("-3.967072"), new BigDecimal("39.708678"), "in the county",
                            "Quarrying", "5-9 years", "Individual", "Foundations, walling, paving, flooring",
                            "1-10 men", "Around kashani area, Zimlati", "500-1000 tonnes", "Machines aree used to extract"),

                    // Row 13
                    new MaterialSite(13, "001B", "Coral reef stones", "Shanzu-Maweni",
                            new BigDecimal("-3.9779"), new BigDecimal("39.729153"), "in the county",
                            "Small industry, quarrying", "10-19 years", "Community", "Walling , paving",
                            "51-100 men", null, "500-1000 tonnes", "Area is owned by community"),

                    // Row 14
                    new MaterialSite(14, "001B", "Blocks", "Shanzu-Maweni",
                            new BigDecimal("-3.9779"), new BigDecimal("39.729153"), "in the county",
                            "Small industry", "10-19 years", "Community", "Foundations, walling , paving",
                            "51-100 men", null, "500-1000 tonnes", "Area is owned by community"),

                    // Row 15
                    new MaterialSite(15, "001B", "Sand", "Shanzu-Maweni",
                            new BigDecimal("-3.9779"), new BigDecimal("39.729153"), "in the county",
                            "Small industry", "10-19 years", "Community", "Foundations, walling , paving, maintenance, flooring",
                            "51-100 men", null, "500-1000 tonnes", "Area is owned by community"),

                    // Row 16
                    new MaterialSite(16, "001B", "Metal Windows and doors", "Mwakurunge-Ugatunzi kiembeni",
                            new BigDecimal("-3.984019"), new BigDecimal("39.6939"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Walling, roofing",
                            "1-10 men", null, "150-500 units", "Workshop makes windows and doors"),

                    // Row 17
                    new MaterialSite(17, "001B", "Chips blocks", "Bamburi-Balawa",
                            new BigDecimal("-3.9833"), new BigDecimal("39.714746"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Decoration, interior design",
                            "1-10 men", null, "150-500 units", null),

                    // Row 18
                    new MaterialSite(18, "001B", "Balcony balustrades", "Bamburi-Balawa",
                            new BigDecimal("-3.9833"), new BigDecimal("39.714746"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Decoration, interior design",
                            "1-10 men", null, "150-500 units", "The individuals produce it themselves"),

                    // Row 19
                    new MaterialSite(19, "001B", "Ventilation blocks", "Mtopanga-Siowatu",
                            new BigDecimal("-4.014474"), new BigDecimal("39.693133"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Decoration, interior design",
                            "1-10 men", null, "150-500 units", null),

                    // Row 20
                    new MaterialSite(20, "001B", "Glass", "Bamburi- Bamburi mwisho",
                            new BigDecimal("-4.002331"), new BigDecimal("39.700027"), "in the county",
                            "Small industry", "1 year", "Mozain glass hardware and electrical", "Walling, interior design",
                            "1-10 men", null, "1-10 units", "Glass used in making windows and doors"),

                    // Row 21
                    new MaterialSite(21, "001B", "Culvert", "Mtopanga-Siowatu",
                            new BigDecimal("-4.014474"), new BigDecimal("39.693133"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Decoration, interior design",
                            "1-10 men", null, "150-500 units", null),

                    // Row 22
                    new MaterialSite(22, "001B", "Poles", "Mtopanga-Siowatu",
                            new BigDecimal("-4.014474"), new BigDecimal("39.693133"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Walling, roofing, decoration, interior design",
                            "1-10 men", null, "150-500 units", "The individuals produce it themselves"),

                    // Row 23
                    new MaterialSite(23, "001B", "Balusters", "Mtopanga-Siowatu",
                            new BigDecimal("-4.014474"), new BigDecimal("39.693133"), "in the county",
                            "Small industry", "5-9 years", "Individual", "Walling, roofing, decoration, interior design",
                            "1-10 men", null, "150-500 units", "The individuals produce it themselves"),

                    // Row 24
                    new MaterialSite(24, "001B", "Metal Windows and doors", "Mtopanga-stage ya paka",
                            new BigDecimal("-4.028393"), new BigDecimal("39.677455"), "in the county",
                            "Small industry", "5-9 years", "Katungauliah General Works", "Roofing, interior design",
                            "1-10 men", "Jacarand, within mtopanga area , a lot of people are producing", "150-500 units", "The windows , gates and doors are made from metal and wood"),

                    // Row 25
                    new MaterialSite(25, "001B", "Culvetrs", "Bamburi-Mwembeni",
                            new BigDecimal("-4.005788"), new BigDecimal("39.697018"), "in the county",
                            "Small industry", "1 year", "Sirago building contractors LTD", "Roads",
                            "1-10 men", "Around Bamburi area, Around Vescom", "500-1000 units", "This is a branch of Siargo Building contractors LTD"),

                    // Row 26
                    new MaterialSite(26, "001B", "Concrete", "Bamburi-Mwembeni",
                            new BigDecimal("-4.005788"), new BigDecimal("39.697018"), "in the county",
                            "Small industry", "1 year", "Sirago building contractors LTD", "Roofing, roads, interoir design",
                            "1-10 men", "Around Bamburi area, Around Vescom", "500-1000 units", "This is a branch of Siargo Building contractors LTD"),

                    // Row 27
                    new MaterialSite(27, "001B", "Road caps", "Bamburi-Mwembeni",
                            new BigDecimal("-4.005788"), new BigDecimal("39.697018"), "in the county",
                            "Small industry", "1 year", "Sirago building contractors LTD", "Decoration, roads, interoir design",
                            "1-10 men", "Around Bamburi area, Around Vescom, Bamburi cement", "500-1000 units", "This is a branch of Siargo Building contractors LTD"),

                    // Row 28
                    new MaterialSite(28, "001B", "Cinder blocks", "Bamburi-Mwembeni",
                            new BigDecimal("-4.005788"), new BigDecimal("39.697018"), "in the county",
                            "Small industry", "1 year", "Sirago building contractors LTD", "Foundation, walling, paving",
                            "1-10 men", "Around Bamburi area, Around Vescom, Bamburi cement", "500-1000 units", "This is a branch of Siargo Building contractors LTD"),

                    // Row 29
                    new MaterialSite(29, "001B", "Cement sinks", "Bamburi-Mwembeni",
                            new BigDecimal("-4.005788"), new BigDecimal("39.697018"), "in the county",
                            "Small industry", "1 year", "Sirago building contractors LTD", "Decoration,interior design, sinks",
                            "1-10 men", "Around Bamburi area, Around Vescom, Bamburi cement", "500-1000 units", "This is a branch of Siargo Building contractors LTD, sinks are used in hospitals mostly"),

                    // Row 30
                    new MaterialSite(30, "001B", "Cement", "Bamburi-Bamburi",
                            new BigDecimal("-4.004572"), new BigDecimal("39.71747"), "in the county, other counties, exported",
                            "Large industry", "20-49 years", "Bamburi  Cement factories", "Cement",
                            "Over 100 men. Over 100 women", null, "500-1000 bags", "It is the main product"),

                    // Row 31
                    new MaterialSite(31, "001B", "Poles", "Bamburi-Bamburi",
                            new BigDecimal("-4.004572"), new BigDecimal("39.71747"), "in the county, other counties, exported",
                            "Large industry", "20-49 years", "Bamburi  Cement factories", "Foundations, walling",
                            "Over 100 men. Over 100 women", "Mwembeni,around Bamburi", "500-1000 bags", "Company is privately owned"),

                    // Row 32
                    new MaterialSite(32, "001B", "Sand/coral reefs", "Shanzu-Shanzu",
                            new BigDecimal("-3.977113"), new BigDecimal("39.741586"), "Other counties",
                            "Large industry, quarrying", "10-19 years", "Bamburi cement quarry", "Foundation, roofing, paving, flooring",
                            "Over 100 men.", null, "500-1000 bags", "The land belongs to bamburi cement factory and is running out of sand"),

                    // Row 33
                    new MaterialSite(33, "001B", "Cabro", "Bamburi-Bamburi",
                            new BigDecimal("-4.004572"), new BigDecimal("39.71747"), "in the county, other counties, exported",
                            "Large industry", "10-19 years", "Bamburi cement factory", "Roofing, Decoration, interoir design",
                            "Over 100 men. Over 100 women", null, "500-1000 bags", null),

                    // Row 34
                    new MaterialSite(34, "001B", "Verntilation", "Shanzu-shanzu",
                            new BigDecimal("-3.977113"), new BigDecimal("39.741586"), "In the county",
                            "small industry", "5-9 years", "Individual", "Decoration, interior design",
                            "1-10 men", "Around shanzu", "150-500 units", null)
            );

            materialSiteService.saveAllMaterialSites(initialSites);
            System.out.println("Successfully loaded " + initialSites.size() + " material sites into database");
        } else {
            System.out.println("Material sites data already exists in database");
        }
    }
}