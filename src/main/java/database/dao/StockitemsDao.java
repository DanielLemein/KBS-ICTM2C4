package database.dao;

import database.model.Stockitemholdings;
import database.model.Stockitems;
import database.util.RowLockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockitemsDao {

    private static final Logger logger = LoggerFactory.getLogger(StockitemsDao.class);
    private static StockitemsDao instance = null;

    private StockitemsDao() {}

    public static StockitemsDao getInstance() {
        if(instance == null) {
            instance = new StockitemsDao();
        }
        return instance;
    }


    /**
     * gets stock from item by searching on StockItemID
     * @param con
     * @param stockItemID
     * @param rowLockType
     * @return new Stockitemholdings object
     * @throws SQLException
     */
    public Stockitems getStockByStockItemID(Connection con, int stockItemID, RowLockType rowLockType) throws SQLException {
        String query = rowLockType.getQueryWithLock(
                "SELECT SI.stockItemID, QuantityOnHand, stockItemName " +
                "FROM stockitems AS SI " +
                "LEFT JOIN stockitemholdings AS SIH ON SIH.stockItemID = SI.stockItemID " +
                "WHERE StockItemID = ? "
        );
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, stockItemID);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return getStockitems(rs);
                } else {
                    return null;
                }
            }
        }
    }


    /**
     *
     * @param con
     * @return
     * @throws SQLException
     */
    public List<Stockitems> getAllStockItemHoldings(Connection con) throws SQLException {
        String query = "SELECT SI.stockItemID, QuantityOnHand, stockItemName " +
                "FROM stockitems AS SI " +
                "LEFT JOIN stockitemholdings AS SIH ON SIH.stockItemID = SI.stockItemID " +
                "ORDER BY stockItemID";
        List<Stockitems> stockitems = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement(query)) {
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    stockitems.add(getStockitems(rs));
                }
            }
        }
        return stockitems;
    }
    private static Stockitems getStockitems(ResultSet rs) throws SQLException {
        Stockitemholdings stockitemholdings = new Stockitemholdings(
                rs.getInt("StockItemID"),
                rs.getInt("QuantityOnHand")
        );
        return new Stockitems(
                rs.getInt("StockItemID"),
                rs.getString("StockItemName"),
                stockitemholdings);
    }
}
