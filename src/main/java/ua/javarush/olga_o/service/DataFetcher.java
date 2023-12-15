package ua.javarush.olga_o.service;

import org.hibernate.Session;
import ua.javarush.olga_o.Main;
import ua.javarush.olga_o.domain.City;
import ua.javarush.olga_o.domain.Country;

import java.util.ArrayList;
import java.util.List;

public class DataFetcher {
    private final RelationDbConnector relationDbConnector;

    public DataFetcher(RelationDbConnector relationDbConnector) {
        this.relationDbConnector = relationDbConnector;
    }

    public List<City> fetchData() {
        try (Session session = relationDbConnector.getSessionFactory().getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();
            List<Country> countries = relationDbConnector.getCountryDAO().getAll();

            int totalCount = relationDbConnector.getCityDAO().getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(relationDbConnector.getCityDAO().getItems(i, step));
            }
            session.getTransaction().commit();
            return allCities;
        }
    }
}
