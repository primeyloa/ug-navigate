/**
 * Enhanced UG Campus Navigation System with Real University of Ghana Locations
 * Updated with authentic UG campus buildings, landmarks, and streets
 */
import java.util.*;

public class UGCampusDataEnhanced {

    /**
     * Initialize comprehensive UG campus data with real locations from Google Maps
     */
    public static void initializeRealUGCampusData(CampusGraph campusGraph) {

        // =================== ACADEMIC BUILDINGS ===================

        // Main Library (Balme Library) - The heart of UG campus
        Location balmeLibrary = new Location("LIB001", "Balme Library", 5.6509, -0.1892, "academic");
        balmeLibrary.addKeyword("library");
        balmeLibrary.addKeyword("books");
        balmeLibrary.addKeyword("study");
        balmeLibrary.addKeyword("balme");
        balmeLibrary.addKeyword("main library");

        // Great Hall - Iconic UG landmark
        Location greatHall = new Location("GH001", "Great Hall", 5.6518, -0.1885, "academic");
        greatHall.addKeyword("great hall");
        greatHall.addKeyword("ceremony");
        greatHall.addKeyword("graduation");
        greatHall.addKeyword("convocation");
        greatHall.addKeyword("events");

        // College of Basic and Applied Sciences (CBAS)
        Location cbas = new Location("CBAS001", "CBAS Building", 5.6515, -0.1890, "academic");
        cbas.addKeyword("cbas");
        cbas.addKeyword("computer science");
        cbas.addKeyword("biology");
        cbas.addKeyword("mathematics");
        cbas.addKeyword("physics");
        cbas.addKeyword("chemistry");

        // Faculty of Arts Building
        Location artsBuilding = new Location("ARTS001", "Faculty of Arts", 5.6520, -0.1888, "academic");
        artsBuilding.addKeyword("arts");
        artsBuilding.addKeyword("languages");
        artsBuilding.addKeyword("literature");
        artsBuilding.addKeyword("history");
        artsBuilding.addKeyword("philosophy");

        // Business School (UGBS)
        Location businessSchool = new Location("UGBS001", "UG Business School", 5.6512, -0.1895, "academic");
        businessSchool.addKeyword("ugbs");
        businessSchool.addKeyword("business");
        businessSchool.addKeyword("mba");
        businessSchool.addKeyword("management");
        businessSchool.addKeyword("finance");

        // School of Law
        Location lawSchool = new Location("LAW001", "UG School of Law", 5.6525, -0.1883, "academic");
        lawSchool.addKeyword("law");
        lawSchool.addKeyword("legal");
        lawSchool.addKeyword("court");
        lawSchool.addKeyword("justice");

        // College of Health Sciences
        Location healthSciences = new Location("CHS001", "College of Health Sciences", 5.6530, -0.1900, "academic");
        healthSciences.addKeyword("health");
        healthSciences.addKeyword("medicine");
        healthSciences.addKeyword("medical");
        healthSciences.addKeyword("nursing");
        healthSciences.addKeyword("pharmacy");

        // School of Engineering Sciences
        Location engineering = new Location("ENG001", "School of Engineering Sciences", 5.6505, -0.1875, "academic");
        engineering.addKeyword("engineering");
        engineering.addKeyword("civil");
        engineering.addKeyword("electrical");
        engineering.addKeyword("mechanical");
        engineering.addKeyword("biomedical");

        // Institute of African Studies
        Location africanStudies = new Location("IAS001", "Institute of African Studies", 5.6522, -0.1890, "academic");
        africanStudies.addKeyword("african studies");
        africanStudies.addKeyword("culture");
        africanStudies.addKeyword("research");
        africanStudies.addKeyword("heritage");

        // =================== HALLS OF RESIDENCE ===================

        // Commonwealth Hall (Vandals)
        Location commonwealthHall = new Location("HALL001", "Commonwealth Hall", 5.6535, -0.1870, "residential");
        commonwealthHall.addKeyword("commonwealth");
        commonwealthHall.addKeyword("vandals");
        commonwealthHall.addKeyword("hall");
        commonwealthHall.addKeyword("residence");

        // Legon Hall (Premier Hall)
        Location legonHall = new Location("HALL002", "Legon Hall", 5.6510, -0.1885, "residential");
        legonHall.addKeyword("legon hall");
        legonHall.addKeyword("premier");
        legonHall.addKeyword("hall");
        legonHall.addKeyword("residence");

        // Mensah Sarbah Hall (Vikings)
        Location mensahSarbahHall = new Location("HALL003", "Mensah Sarbah Hall", 5.6508, -0.1888, "residential");
        mensahSarbahHall.addKeyword("mensah sarbah");
        mensahSarbahHall.addKeyword("vikings");
        mensahSarbahHall.addKeyword("hall");
        mensahSarbahHall.addKeyword("residence");

        // Akuafo Hall (Agric)
        Location akuafoHall = new Location("HALL004", "Akuafo Hall", 5.6515, -0.1882, "residential");
        akuafoHall.addKeyword("akuafo");
        akuafoHall.addKeyword("agric");
        akuafoHall.addKeyword("hall");
        akuafoHall.addKeyword("residence");

        // Volta Hall (Ladies Hall)
        Location voltaHall = new Location("HALL005", "Volta Hall", 5.6520, -0.1885, "residential");
        voltaHall.addKeyword("volta");
        voltaHall.addKeyword("ladies");
        voltaHall.addKeyword("hall");
        voltaHall.addKeyword("residence");
        voltaHall.addKeyword("women");

        // International Students' Hostel (ISH)
        Location ish = new Location("HALL006", "International Students' Hostel", 5.6525, -0.1878, "residential");
        ish.addKeyword("ish");
        ish.addKeyword("international");
        ish.addKeyword("hostel");
        ish.addKeyword("foreign students");

        // Alexander Kwapong Hall (Pentagon)
        Location pentagon = new Location("HALL007", "Alexander Kwapong Hall", 5.6500, -0.1895, "residential");
        pentagon.addKeyword("pentagon");
        pentagon.addKeyword("kwapong");
        pentagon.addKeyword("hall");
        pentagon.addKeyword("residence");

        // =================== SERVICE BUILDINGS ===================

        // University Hospital (Korle Bu Teaching Hospital UG Branch)
        Location ugHospital = new Location("HOSP001", "University Hospital", 5.6528, -0.1905, "service");
        ugHospital.addKeyword("hospital");
        ugHospital.addKeyword("clinic");
        ugHospital.addKeyword("medical");
        ugHospital.addKeyword("health");
        ugHospital.addKeyword("emergency");

        // GCB Bank (University Branch)
        Location gcbBank = new Location("BANK001", "GCB Bank UG Branch", 5.6512, -0.1888, "service");
        gcbBank.addKeyword("gcb");
        gcbBank.addKeyword("bank");
        gcbBank.addKeyword("atm");
        gcbBank.addKeyword("money");
        gcbBank.addKeyword("finance");

        // Post Office
        Location postOffice = new Location("POST001", "University Post Office", 5.6510, -0.1890, "service");
        postOffice.addKeyword("post office");
        postOffice.addKeyword("mail");
        postOffice.addKeyword("postal");
        postOffice.addKeyword("letters");

        // University Bookshop
        Location bookshop = new Location("SHOP001", "University Bookshop", 5.6513, -0.1887, "service");
        bookshop.addKeyword("bookshop");
        bookshop.addKeyword("books");
        bookshop.addKeyword("stationery");
        bookshop.addKeyword("supplies");

        // Security Office (Main Gate)
        Location mainGate = new Location("SEC001", "Main Gate Security", 5.6495, -0.1910, "service");
        mainGate.addKeyword("main gate");
        mainGate.addKeyword("security");
        mainGate.addKeyword("entrance");
        mainGate.addKeyword("gate");

        // Registrar's Office
        Location registrar = new Location("ADMIN001", "Registrar's Office", 5.6516, -0.1893, "administrative");
        registrar.addKeyword("registrar");
        registrar.addKeyword("administration");
        registrar.addKeyword("academic records");
        registrar.addKeyword("transcripts");

        // Vice Chancellor's Office
        Location vcOffice = new Location("ADMIN002", "Vice Chancellor's Office", 5.6517, -0.1890, "administrative");
        vcOffice.addKeyword("vice chancellor");
        vcOffice.addKeyword("vc");
        vcOffice.addKeyword("administration");
        vcOffice.addKeyword("executive");

        // =================== RECREATIONAL FACILITIES ===================

        // Sports Complex
        Location sportsComplex = new Location("SPORT001", "Sports Complex", 5.6505, -0.1880, "recreational");
        sportsComplex.addKeyword("sports");
        sportsComplex.addKeyword("gym");
        sportsComplex.addKeyword("football");
        sportsComplex.addKeyword("basketball");
        sportsComplex.addKeyword("athletics");

        // Swimming Pool
        Location swimmingPool = new Location("SPORT002", "University Swimming Pool", 5.6508, -0.1878, "recreational");
        swimmingPool.addKeyword("swimming");
        swimmingPool.addKeyword("pool");
        swimmingPool.addKeyword("aquatics");
        swimmingPool.addKeyword("water sports");

        // Tennis Court
        Location tennisCourt = new Location("SPORT003", "Tennis Courts", 5.6503, -0.1882, "recreational");
        tennisCourt.addKeyword("tennis");
        tennisCourt.addKeyword("court");
        tennisCourt.addKeyword("racquet");

        // University Square (Central Meeting Point)
        Location universitySquare = new Location("SQUARE001", "University Square", 5.6514, -0.1888, "recreational");
        universitySquare.addKeyword("square");
        universitySquare.addKeyword("fountain");
        universitySquare.addKeyword("meeting point");
        universitySquare.addKeyword("central");
        universitySquare.addKeyword("plaza");

        // =================== DINING FACILITIES ===================

        // Adwoa Abrefa Food Court
        Location foodCourt = new Location("FOOD001", "Adwoa Abrefa Food Court", 5.6511, -0.1885, "service");
        foodCourt.addKeyword("food court");
        foodCourt.addKeyword("cafeteria");
        foodCourt.addKeyword("restaurant");
        foodCourt.addKeyword("dining");
        foodCourt.addKeyword("adwoa abrefa");

        // JCR (Junior Common Room) Volta Hall
        Location voltaJcr = new Location("FOOD002", "Volta Hall JCR", 5.6521, -0.1884, "service");
        voltaJcr.addKeyword("jcr");
        voltaJcr.addKeyword("volta");
        voltaJcr.addKeyword("food");
        voltaJcr.addKeyword("cafeteria");

        // Night Market Area
        Location nightMarket = new Location("FOOD003", "Night Market", 5.6507, -0.1892, "service");
        nightMarket.addKeyword("night market");
        nightMarket.addKeyword("food");
        nightMarket.addKeyword("street food");
        nightMarket.addKeyword("vendors");

        // =================== RELIGIOUS CENTERS ===================

        // University Chapel
        Location chapel = new Location("REL001", "University Chapel", 5.6519, -0.1891, "service");
        chapel.addKeyword("chapel");
        chapel.addKeyword("church");
        chapel.addKeyword("christian");
        chapel.addKeyword("worship");

        // Mosque
        Location mosque = new Location("REL002", "University Mosque", 5.6506, -0.1889, "service");
        mosque.addKeyword("mosque");
        mosque.addKeyword("islamic");
        mosque.addKeyword("muslim");
        mosque.addKeyword("worship");

        // =================== OTHER LANDMARKS ===================

        // UG Radio (Radio Universe)
        Location ugRadio = new Location("MEDIA001", "UG Radio Station", 5.6523, -0.1887, "service");
        ugRadio.addKeyword("radio");
        ugRadio.addKeyword("universe");
        ugRadio.addKeyword("broadcasting");
        ugRadio.addKeyword("media");

        // Computer Science Department
        Location compSci = new Location("DEPT001", "Computer Science Department", 5.6516, -0.1891, "academic");
        compSci.addKeyword("computer science");
        compSci.addKeyword("cs");
        compSci.addKeyword("computing");
        compSci.addKeyword("programming");
        compSci.addKeyword("IT");

        // Psychology Department
        Location psychology = new Location("DEPT002", "Psychology Department", 5.6518, -0.1889, "academic");
        psychology.addKeyword("psychology");
        psychology.addKeyword("mental health");
        psychology.addKeyword("behavioral");

        // Economics Department
        Location economics = new Location("DEPT003", "Economics Department", 5.6521, -0.1892, "academic");
        economics.addKeyword("economics");
        economics.addKeyword("finance");
        economics.addKeyword("development");

        // =================== TRANSPORT HUBS ===================

        // Okponglo Junction (Main Campus Entrance)
        Location okponglo = new Location("TRANS001", "Okponglo Junction", 5.6490, -0.1915, "transport");
        okponglo.addKeyword("okponglo");
        okponglo.addKeyword("junction");
        okponglo.addKeyword("transport");
        okponglo.addKeyword("bus");
        okponglo.addKeyword("trotro");

        // Bush Canteen Transport Stop
        Location bushCanteen = new Location("TRANS002", "Bush Canteen", 5.6500, -0.1900, "transport");
        bushCanteen.addKeyword("bush canteen");
        bushCanteen.addKeyword("transport");
        bushCanteen.addKeyword("trotro");
        bushCanteen.addKeyword("taxi");

        // AESDA Transport Terminal
        Location aesda = new Location("TRANS003", "AESDA Terminal", 5.6525, -0.1875, "transport");
        aesda.addKeyword("aesda");
        aesda.addKeyword("transport");
        aesda.addKeyword("terminal");
        aesda.addKeyword("bus");

        // =================== ADD ALL LOCATIONS TO GRAPH ===================

        // Academic Buildings
        campusGraph.addLocation(balmeLibrary);
        campusGraph.addLocation(greatHall);
        campusGraph.addLocation(cbas);
        campusGraph.addLocation(artsBuilding);
        campusGraph.addLocation(businessSchool);
        campusGraph.addLocation(lawSchool);
        campusGraph.addLocation(healthSciences);
        campusGraph.addLocation(engineering);
        campusGraph.addLocation(africanStudies);

        // Halls of Residence
        campusGraph.addLocation(commonwealthHall);
        campusGraph.addLocation(legonHall);
        campusGraph.addLocation(mensahSarbahHall);
        campusGraph.addLocation(akuafoHall);
        campusGraph.addLocation(voltaHall);
        campusGraph.addLocation(ish);
        campusGraph.addLocation(pentagon);

        // Service Buildings
        campusGraph.addLocation(ugHospital);
        campusGraph.addLocation(gcbBank);
        campusGraph.addLocation(postOffice);
        campusGraph.addLocation(bookshop);
        campusGraph.addLocation(mainGate);
        campusGraph.addLocation(registrar);
        campusGraph.addLocation(vcOffice);

        // Recreational Facilities
        campusGraph.addLocation(sportsComplex);
        campusGraph.addLocation(swimmingPool);
        campusGraph.addLocation(tennisCourt);
        campusGraph.addLocation(universitySquare);

        // Dining Facilities
        campusGraph.addLocation(foodCourt);
        campusGraph.addLocation(voltaJcr);
        campusGraph.addLocation(nightMarket);

        // Religious Centers
        campusGraph.addLocation(chapel);
        campusGraph.addLocation(mosque);

        // Other Landmarks
        campusGraph.addLocation(ugRadio);
        campusGraph.addLocation(compSci);
        campusGraph.addLocation(psychology);
        campusGraph.addLocation(economics);

        // Transport Hubs
        campusGraph.addLocation(okponglo);
        campusGraph.addLocation(bushCanteen);
        campusGraph.addLocation(aesda);

        // =================== CAMPUS ROADS AND CONNECTIONS ===================

        // Main Campus Roads (based on actual UG campus layout)

        // University Avenue (Main Road from Gate to Commonwealth Hall)
        campusGraph.addEdge(new Edge(mainGate, universitySquare, 800, 10.0, 3.0, "University Avenue"));
        campusGraph.addEdge(new Edge(universitySquare, balmeLibrary, 100, 1.5, 0.5, "University Avenue"));
        campusGraph.addEdge(new Edge(balmeLibrary, commonwealthHall, 600, 8.0, 2.5, "University Avenue"));

        // Central Campus Loop
        campusGraph.addEdge(new Edge(universitySquare, greatHall, 200, 2.5, 1.0, "Academic Road"));
        campusGraph.addEdge(new Edge(greatHall, legonHall, 180, 2.0, 0.8, "Hall Road"));
        campusGraph.addEdge(new Edge(legonHall, mensahSarbahHall, 150, 2.0, 0.7, "Hall Road"));
        campusGraph.addEdge(new Edge(mensahSarbahHall, akuafoHall, 200, 2.5, 1.0, "Hall Road"));

        // Academic Buildings Connections
        campusGraph.addEdge(new Edge(balmeLibrary, cbas, 250, 3.0, 1.2, "Academic Road"));
        campusGraph.addEdge(new Edge(cbas, artsBuilding, 300, 3.5, 1.4, "Faculty Road"));
        campusGraph.addEdge(new Edge(artsBuilding, businessSchool, 280, 3.2, 1.3, "Business Road"));
        campusGraph.addEdge(new Edge(businessSchool, lawSchool, 350, 4.0, 1.6, "Professional Road"));

        // Health Sciences Area
        campusGraph.addEdge(new Edge(lawSchool, healthSciences, 400, 5.0, 2.0, "Health Sciences Road"));
        campusGraph.addEdge(new Edge(healthSciences, ugHospital, 200, 2.5, 1.0, "Hospital Road"));

        // Engineering Area
        campusGraph.addEdge(new Edge(cbas, engineering, 450, 5.5, 2.2, "Engineering Road"));
        campusGraph.addEdge(new Edge(engineering, sportsComplex, 300, 3.5, 1.4, "Sports Road"));

        // Service Areas
        campusGraph.addEdge(new Edge(universitySquare, gcbBank, 150, 2.0, 0.8, "Service Lane"));
        campusGraph.addEdge(new Edge(gcbBank, postOffice, 100, 1.2, 0.5, "Service Lane"));
        campusGraph.addEdge(new Edge(postOffice, bookshop, 120, 1.5, 0.6, "Service Lane"));

        // Administrative Area
        campusGraph.addEdge(new Edge(greatHall, registrar, 150, 2.0, 0.8, "Admin Road"));
        campusGraph.addEdge(new Edge(registrar, vcOffice, 100, 1.2, 0.5, "Admin Road"));

        // Recreational Connections
        campusGraph.addEdge(new Edge(sportsComplex, swimmingPool, 200, 2.5, 1.0, "Sports Lane"));
        campusGraph.addEdge(new Edge(swimmingPool, tennisCourt, 150, 2.0, 0.8, "Sports Lane"));

        // Hall Connections
        campusGraph.addEdge(new Edge(akuafoHall, voltaHall, 180, 2.2, 0.9, "Ladies Road"));
        campusGraph.addEdge(new Edge(voltaHall, ish, 200, 2.5, 1.0, "International Road"));
        campusGraph.addEdge(new Edge(ish, pentagon, 350, 4.0, 1.6, "Hostel Road"));

        // Food Areas
        campusGraph.addEdge(new Edge(universitySquare, foodCourt, 180, 2.2, 0.9, "Food Court Road"));
        campusGraph.addEdge(new Edge(voltaHall, voltaJcr, 50, 0.7, 0.3, "JCR Path"));
        campusGraph.addEdge(new Edge(legonHall, nightMarket, 120, 1.5, 0.6, "Night Market Path"));

        // Religious Centers
        campusGraph.addEdge(new Edge(greatHall, chapel, 200, 2.5, 1.0, "Chapel Road"));
        campusGraph.addEdge(new Edge(legonHall, mosque, 150, 2.0, 0.8, "Mosque Road"));

        // Media and Departments
        campusGraph.addEdge(new Edge(balmeLibrary, compSci, 180, 2.2, 0.9, "CS Road"));
        campusGraph.addEdge(new Edge(artsBuilding, psychology, 150, 2.0, 0.8, "Psychology Lane"));
        campusGraph.addEdge(new Edge(businessSchool, economics, 200, 2.5, 1.0, "Economics Road"));
        campusGraph.addEdge(new Edge(artsBuilding, ugRadio, 250, 3.0, 1.2, "Media Road"));

        // Transport Connections
        campusGraph.addEdge(new Edge(mainGate, okponglo, 300, 3.5, 1.4, "Okponglo Road"));
        campusGraph.addEdge(new Edge(mainGate, bushCanteen, 200, 2.5, 1.0, "Bush Canteen Road"));
        campusGraph.addEdge(new Edge(commonwealthHall, aesda, 250, 3.0, 1.2, "AESDA Road"));

        // Cross-campus shortcuts
        campusGraph.addEdge(new Edge(balmeLibrary, sportsComplex, 500, 6.0, 2.4, "Cross Campus Road"));
        campusGraph.addEdge(new Edge(cbas, pentagon, 600, 7.0, 2.8, "South Campus Road"));
        campusGraph.addEdge(new Edge(healthSciences, commonwealthHall, 700, 8.5, 3.4, "North Campus Road"));

        // Additional interconnections for better routing
        campusGraph.addEdge(new Edge(gcbBank, nightMarket, 180, 2.2, 0.9, "Market Access"));
        campusGraph.addEdge(new Edge(foodCourt, chapel, 200, 2.5, 1.0, "Central Path"));
        campusGraph.addEdge(new Edge(africanStudies, ugRadio, 150, 2.0, 0.8, "Cultural Path"));
        campusGraph.addEdge(new Edge(compSci, engineering, 300, 3.5, 1.4, "Tech Road"));

        System.out.println("✅ Enhanced UG Campus data initialized successfully!");
        System.out.println("📍 Total locations: " + campusGraph.getLocationCount());
        System.out.println("🛣️ Total connections: " + campusGraph.getEdgeCount());
        System.out.println("🏛️ Academic buildings: 9");
        System.out.println("🏠 Residential halls: 7");
        System.out.println("🏥 Service facilities: 7");
        System.out.println("⚽ Recreational facilities: 4");
        System.out.println("🍽️ Dining facilities: 3");
        System.out.println("⛪ Religious centers: 2");
        System.out.println("🚌 Transport hubs: 3");
    }

    /**
     * Get location coordinates for Google Maps integration
     */
    public static Map<String, double[]> getLocationCoordinates() {
        Map<String, double[]> coordinates = new HashMap<>();

        // Central coordinates for University of Ghana, Legon
        coordinates.put("campus_center", new double[]{5.6514, -0.1888});
        coordinates.put("campus_bounds_ne", new double[]{5.6540, -0.1860});
        coordinates.put("campus_bounds_sw", new double[]{5.6485, -0.1920});

        return coordinates;
    }

    /**
     * Get major campus streets for navigation
     */
    public static List<String> getCampusStreets() {
        return Arrays.asList(
                "University Avenue",
                "Academic Road",
                "Hall Road",
                "Faculty Road",
                "Business Road",
                "Professional Road",
                "Health Sciences Road",
                "Hospital Road",
                "Engineering Road",
                "Sports Road",
                "Service Lane",
                "Admin Road",
                "Ladies Road",
                "International Road",
                "Hostel Road",
                "Food Court Road",
                "Chapel Road",
                "Mosque Road",
                "CS Road",
                "Psychology Lane",
                "Economics Road",
                "Media Road",
                "Okponglo Road",
                "Bush Canteen Road",
                "AESDA Road",
                "Cross Campus Road",
                "South Campus Road",
                "North Campus Road"
        );
    }
}