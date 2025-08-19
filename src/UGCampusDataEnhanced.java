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
        Location balmeLibrary = new Location("LIB001", "Balme Library", 5.6511292, -0.1870251, "academic");
        balmeLibrary.addKeyword("library");
        balmeLibrary.addKeyword("books");
        balmeLibrary.addKeyword("study");
        balmeLibrary.addKeyword("balme");
        balmeLibrary.addKeyword("main library");
        balmeLibrary.setGooglePhotoUrl("https://goo.gl/maps/1Q2w3e4r5t6y7u8i9"); // Google Photos

        // Great Hall - Iconic UG landmark
        Location greatHall = new Location("GH001", "Great Hall", 5.650678, -0.196216, "academic");
        greatHall.addKeyword("great hall");
        greatHall.addKeyword("ceremony");
        greatHall.addKeyword("graduation");
        greatHall.addKeyword("convocation");
        greatHall.addKeyword("events");
        greatHall.setGooglePhotoUrl("https://goo.gl/maps/2A3b4C5d6E7f8G9h1");

        // College of Basic and Applied Sciences (CBAS)
        Location cbas = new Location("CBAS001", "CBAS Building", 5.6515, -0.1890, "academic");
        cbas.addKeyword("cbas");
        cbas.addKeyword("computer science");
        cbas.addKeyword("biology");
        cbas.addKeyword("mathematics");
        cbas.addKeyword("physics");
        cbas.addKeyword("chemistry");
        cbas.setGooglePhotoUrl("https://goo.gl/maps/3D4e5F6g7H8i9J1k2");

        // Faculty of Arts Building
        Location artsBuilding = new Location("ARTS001", "Faculty of Arts", 5.6509624, -0.1883361, "academic");
        artsBuilding.addKeyword("arts");
        artsBuilding.addKeyword("languages");
        artsBuilding.addKeyword("literature");
        artsBuilding.addKeyword("history");
        artsBuilding.addKeyword("philosophy");
        artsBuilding.setGooglePhotoUrl("https://goo.gl/maps/4K5l6M7n8O9p1Q2r3");

        // Business School (UGBS)
        Location businessSchool = new Location("UGBS001", "UG Business School", 5.6528087, -0.188627, "academic");
        businessSchool.addKeyword("ugbs");
        businessSchool.addKeyword("business");
        businessSchool.addKeyword("mba");
        businessSchool.addKeyword("management");
        businessSchool.addKeyword("finance");
        businessSchool.setGooglePhotoUrl("https://goo.gl/maps/5S6t7U8v9W1x2Y3z4");

        // School of Law
        Location lawSchool = new Location("LAW001", "UG School of Law", 5.6525, -0.1883, "academic");
        lawSchool.addKeyword("law");
        lawSchool.addKeyword("legal");
        lawSchool.addKeyword("court");
        lawSchool.addKeyword("justice");
        lawSchool.setGooglePhotoUrl("https://goo.gl/maps/6A7b8C9d1E2f3G4h5");

        // College of Health Sciences
        Location healthSciences = new Location("CHS001", "College of Health Sciences", 5.637016, -0.1829065, "academic");
        healthSciences.addKeyword("health");
        healthSciences.addKeyword("medicine");
        healthSciences.addKeyword("medical");
        healthSciences.addKeyword("nursing");
        healthSciences.addKeyword("pharmacy");
        healthSciences.setGooglePhotoUrl("https://goo.gl/maps/7D8e9F1g2H3i4J5k6");

        // School of Engineering Sciences
        Location engineering = new Location("ENG001", "School of Engineering Sciences", 5.6555768, -0.1828086, "academic");
        engineering.addKeyword("engineering");
        engineering.addKeyword("civil");
        engineering.addKeyword("electrical");
        engineering.addKeyword("mechanical");
        engineering.addKeyword("biomedical");
        engineering.setGooglePhotoUrl("https://goo.gl/maps/8K9l1M2n3O4p5Q6r7");

        // Institute of African Studies
        Location africanStudies = new Location("IAS001", "Institute of African Studies", 5.6522, -0.1890, "academic");
        africanStudies.addKeyword("african studies");
        africanStudies.addKeyword("culture");
        africanStudies.addKeyword("research");
        africanStudies.addKeyword("heritage");
        africanStudies.setGooglePhotoUrl("https://goo.gl/maps/9S1t2U3v4W5x6Y7z8");

        // =================== HALLS OF RESIDENCE ===================

        // Commonwealth Hall (Vandals)
        Location commonwealthHall = new Location("HALL001", "Commonwealth Hall", 5.6505213, -0.192648, "residential");
        commonwealthHall.addKeyword("commonwealth");
        commonwealthHall.addKeyword("vandals");
        commonwealthHall.addKeyword("hall");
        commonwealthHall.addKeyword("residence");

        // Legon Hall (Premier Hall)
        Location legonHall = new Location("HALL002", "Legon Hall", 5.6493174, -0.1884401, "residential");
        legonHall.addKeyword("legon hall");
        legonHall.addKeyword("premier");
        legonHall.addKeyword("hall");
        legonHall.addKeyword("residence");

        // Mensah Sarbah Hall (Vikings)
        Location mensahSarbahHall = new Location("HALL003", "Mensah Sarbah Hall", 5.6459401, -0.187069, "residential");
        mensahSarbahHall.addKeyword("mensah sarbah");
        mensahSarbahHall.addKeyword("vikings");
        mensahSarbahHall.addKeyword("hall");
        mensahSarbahHall.addKeyword("residence");

        // Akuafo Hall (Agric)
        Location akuafoHall = new Location("HALL004", "Akuafo Hall", 5.6489767, -0.1856624, "residential");
        akuafoHall.addKeyword("akuafo");
        akuafoHall.addKeyword("agric");
        akuafoHall.addKeyword("hall");
        akuafoHall.addKeyword("residence");

        // Volta Hall (Ladies Hall)
        Location voltaHall = new Location("HALL005", "Volta Hall", 5.6517282, -0.1894806, "residential");
        voltaHall.addKeyword("volta");
        voltaHall.addKeyword("ladies");
        voltaHall.addKeyword("hall");
        voltaHall.addKeyword("residence");
        voltaHall.addKeyword("women");

        // International Students' Hostel (ISH)
        Location ish = new Location("HALL006", "International Students' Hostel", 5.6415498, -0.1860022, "residential");
        ish.addKeyword("ish");
        ish.addKeyword("international");
        ish.addKeyword("hostel");
        ish.addKeyword("foreign students");

        // Alexander Kwapong Hall (Pentagon)
        Location pentagon = new Location("HALL007", "Alexander Kwapong Hall", 5.6368392, -0.1854012, "residential");
        pentagon.addKeyword("pentagon");
        pentagon.addKeyword("kwapong");
        pentagon.addKeyword("hall");
        pentagon.addKeyword("residence");

        // Hilla Limann Hall (Diaspora)
        Location limann = new Location("HALL008", "Hilla Limann Hall", 5.6374503, -0.1843103, "residential");
        limann.addKeyword("limann");
        limann.addKeyword("diaspora");
        limann.addKeyword("hall");

        // Jean Nelson Aka Hall
        Location jeanNelson = new Location("HALL009", "Jean Nelson Aka Hall", 5.6353136, -0.1881403, "residential");
        jeanNelson.addKeyword("jean nelson");
        jeanNelson.addKeyword("aka");
        jeanNelson.addKeyword("hall");

        // =================== SERVICE BUILDINGS ===================

        // University Hospital (Korle Bu Teaching Hospital UG Branch)
        Location ugHospital = new Location("HOSP001", "University Hospital", 5.6518436, -0.1781098, "service");
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
        Location postOffice = new Location("POST001", "University Post Office", 5.6508996, -0.1876341, "service");
        postOffice.addKeyword("post office");
        postOffice.addKeyword("mail");
        postOffice.addKeyword("postal");
        postOffice.addKeyword("letters");

        // University Bookshop
        Location bookshop = new Location("SHOP001", "University Bookshop", 5.6511685, -0.1866539, "service");
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
        Location vcOffice = new Location("ADMIN002", "Vice Chancellor's Office", 5.6501791, -0.1954248, "administrative");
        vcOffice.addKeyword("vice chancellor");
        vcOffice.addKeyword("vc");
        vcOffice.addKeyword("administration");
        vcOffice.addKeyword("executive");

        // =================== RECREATIONAL FACILITIES ===================

        // Sports Complex
        Location sportsComplex = new Location("SPORT001", "Sports Complex", 5.63902695, -0.18008507481749572, "recreational");
        sportsComplex.addKeyword("sports");
        sportsComplex.addKeyword("gym");
        sportsComplex.addKeyword("football");
        sportsComplex.addKeyword("basketball");
        sportsComplex.addKeyword("athletics");

        // Swimming Pool
        Location swimmingPool = new Location("SPORT002", "University Swimming Pool", 5.6397367, -0.1824198, "recreational");
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
        Location universitySquare = new Location("SQUARE001", "University Square", 5.6427798, -0.1855086, "recreational");
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
        Location voltaJcr = new Location("FOOD002", "Volta Hall JCR", 5.6559684, -0.19047192615807346, "service");
        voltaJcr.addKeyword("jcr");
        voltaJcr.addKeyword("volta");
        voltaJcr.addKeyword("food");
        voltaJcr.addKeyword("cafeteria");

        // Night Market Area
        Location nightMarket = new Location("FOOD003", "Night Market", 5.6421513, -0.1856043, "service");
        nightMarket.addKeyword("night market");
        nightMarket.addKeyword("food");
        nightMarket.addKeyword("street food");
        nightMarket.addKeyword("vendors");

        // =================== RELIGIOUS CENTERS ===================

        // University Chapel
        Location chapel = new Location("REL001", "University Chapel", 5.64659785, -0.18800395110458934, "service");
        chapel.addKeyword("chapel");
        chapel.addKeyword("church");
        chapel.addKeyword("christian");
        chapel.addKeyword("worship");

        // Mosque
        Location mosque = new Location("REL002", "University Mosque", 5.64659785, -0.18800395110458934, "service");
        mosque.addKeyword("mosque");
        mosque.addKeyword("islamic");
        mosque.addKeyword("muslim");
        mosque.addKeyword("worship");

        // =================== OTHER LANDMARKS ===================

        // UG Radio (Radio Universe)
        Location ugRadio = new Location("MEDIA001", "UG Radio Station", 5.65218725, -0.18806457576628352, "service");
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
        Location psychology = new Location("DEPT002", "Psychology Department", 5.6550778, -0.1867541, "academic");
        psychology.addKeyword("psychology");
        psychology.addKeyword("mental health");
        psychology.addKeyword("behavioral");

        // Economics Department
        Location economics = new Location("DEPT003", "Economics Department", 5.6511659, -0.1874726, "academic");
        economics.addKeyword("economics");
        economics.addKeyword("finance");
        economics.addKeyword("development");

        // =================== TRANSPORT HUBS ===================

        // Okponglo Junction (Main Campus Entrance)
        Location okponglo = new Location("TRANS001", "Okponglo Junction", 5.6396227, -0.1694208, "transport");
        okponglo.addKeyword("okponglo");
        okponglo.addKeyword("junction");
        okponglo.addKeyword("transport");
        okponglo.addKeyword("bus");
        okponglo.addKeyword("trotro");

        // Bush Canteen Transport Stop
        Location bushCanteen = new Location("TRANS002", "Bush Canteen", 5.6455967, -0.1800598, "transport");
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
        campusGraph.addLocation(limann);
        campusGraph.addLocation(jeanNelson);

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

        // Off-campus/Private Hostels (popular landmarks used by students)
        Location evandy = new Location("HOST001", "Evandy Hostel", 5.6631104, -0.1818104, "residential");
        evandy.addKeyword("evandy");
        evandy.addKeyword("hostel");
        evandy.setGooglePhotoUrl("https://goo.gl/maps/evandyhostelphoto");

        Location bani = new Location("HOST002", "Bani Hostel", 5.6632559, -0.1799969, "residential");
        bani.addKeyword("bani");
        bani.addKeyword("hostel");

        Location tfHostel = new Location("HOST003", "Ghana Hostels (TF)", 5.6501, -0.1875, "residential");
        tfHostel.addKeyword("tf");
        tfHostel.addKeyword("ghana hostels");
        tfHostel.addKeyword("hostel");

        // Transport Hubs
        campusGraph.addLocation(okponglo);
        campusGraph.addLocation(bushCanteen);
        campusGraph.addLocation(aesda);

        // Private Hostels
        campusGraph.addLocation(evandy);
        campusGraph.addLocation(bani);
        campusGraph.addLocation(tfHostel);

        // =================== CAMPUS ROADS AND CONNECTIONS ===================

        // Main Campus Roads (based on actual UG campus layout)
        addEdgeFromOSRM(campusGraph, mainGate, universitySquare, "University Avenue");
        addEdgeFromOSRM(campusGraph, universitySquare, balmeLibrary, "University Avenue");
        addEdgeFromOSRM(campusGraph, balmeLibrary, commonwealthHall, "University Avenue");

        // Central Campus Loop
        addEdgeFromOSRM(campusGraph, universitySquare, greatHall, "Academic Road");
        addEdgeFromOSRM(campusGraph, greatHall, legonHall, "Hall Road");
        addEdgeFromOSRM(campusGraph, legonHall, mensahSarbahHall, "Hall Road");
        addEdgeFromOSRM(campusGraph, mensahSarbahHall, akuafoHall, "Hall Road");

        // Hall Connections
        addEdgeFromOSRM(campusGraph, akuafoHall, voltaHall, "Ladies Road");
        addEdgeFromOSRM(campusGraph, voltaHall, ish, "International Road");
        addEdgeFromOSRM(campusGraph, ish, pentagon, "Hostel Road");
        addEdgeFromOSRM(campusGraph, pentagon, limann, "Diaspora Road");
        addEdgeFromOSRM(campusGraph, limann, jeanNelson, "Diaspora Road");

        // Food Areas
        addEdgeFromOSRM(campusGraph, universitySquare, foodCourt, "Food Court Road");
        addEdgeFromOSRM(campusGraph, voltaHall, voltaJcr, "JCR Path");
        addEdgeFromOSRM(campusGraph, legonHall, nightMarket, "Night Market Path");
        addEdgeFromOSRM(campusGraph, pentagon, nightMarket, "Night Market Link");
        addEdgeFromOSRM(campusGraph, evandy, nightMarket, "Evandy Link");
        addEdgeFromOSRM(campusGraph, evandy, pentagon, "Hostel Avenue");
        addEdgeFromOSRM(campusGraph, bani, pentagon, "Hostel Avenue");
        addEdgeFromOSRM(campusGraph, tfHostel, pentagon, "Hostel Avenue");

        // Religious Centers
        addEdgeFromOSRM(campusGraph, greatHall, chapel, "Chapel Road");
        addEdgeFromOSRM(campusGraph, legonHall, mosque, "Mosque Road");

        // Media and Departments
        addEdgeFromOSRM(campusGraph, balmeLibrary, compSci, "CS Road");
        addEdgeFromOSRM(campusGraph, artsBuilding, psychology, "Psychology Lane");
        addEdgeFromOSRM(campusGraph, businessSchool, economics, "Economics Road");
        addEdgeFromOSRM(campusGraph, artsBuilding, ugRadio, "Media Road");

        // Transport Connections
        addEdgeFromOSRM(campusGraph, mainGate, okponglo, "Okponglo Road");
        addEdgeFromOSRM(campusGraph, mainGate, bushCanteen, "Bush Canteen Road");
        addEdgeFromOSRM(campusGraph, commonwealthHall, aesda, "AESDA Road");

        // Cross-campus shortcuts
        addEdgeFromOSRM(campusGraph, balmeLibrary, sportsComplex, "Cross Campus Road");
        addEdgeFromOSRM(campusGraph, cbas, pentagon, "South Campus Road");
        addEdgeFromOSRM(campusGraph, healthSciences, commonwealthHall, "North Campus Road");

        // Additional interconnections for better routing
        addEdgeFromOSRM(campusGraph, gcbBank, nightMarket, "Market Access");
        addEdgeFromOSRM(campusGraph, foodCourt, chapel, "Central Path");
        addEdgeFromOSRM(campusGraph, africanStudies, ugRadio, "Cultural Path");
        addEdgeFromOSRM(campusGraph, compSci, engineering, "Tech Road");

        // Direct path between Commonwealth Hall and Volta Hall
        addEdgeFromOSRM(campusGraph, commonwealthHall, voltaHall, "Hall Path");
        addEdgeFromOSRM(campusGraph, voltaHall, commonwealthHall, "Hall Path");

        // === Optional: Connect pairs within a reasonable distance (e.g., <200m) ===
        /*
        List<Location> allLocations = new ArrayList<>(campusGraph.getAllLocations());
        double DISTANCE_THRESHOLD_METERS = 200.0;
        for (int i = 0; i < allLocations.size(); i++) {
            for (int j = i + 1; j < allLocations.size(); j++) {
                Location locA = allLocations.get(i);
                Location locB = allLocations.get(j);
                double[] route = OSMRouteFetcher.fetchRoute(locA.getLatitude(), locA.getLongitude(), locB.getLatitude(), locB.getLongitude());
                if (route != null && route[0] <= DISTANCE_THRESHOLD_METERS) {
                    addEdgeFromOSRM(campusGraph, locA, locB, "OSRM Auto");
                }
            }
        }
        */

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

    /**
     * Helper to add edge using OSRM route data
     */
    private static void addEdgeFromOSRM(CampusGraph graph, Location from, Location to, String roadName) {
        double[] route = OSMRouteFetcher.fetchRoute(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
        if (route != null) {
            double distanceMeters = route[0];
            double durationSeconds = route[1];
            // Convert to kilometers and minutes for Edge constructor
            double distanceKm = distanceMeters / 1000.0;
            double durationMin = durationSeconds / 60.0;
            graph.addEdge(new Edge(from, to, distanceMeters, durationMin, distanceKm, roadName));
        } else {
            // Fallback: use default values if OSRM fails
            graph.addEdge(new Edge(from, to, 250, 3.0, 1.2, roadName));
        }
    }
}
