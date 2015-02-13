/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mailer;

/**
 *
 * @author blah-blah
 */

import java.sql.*;

public class GetData {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String query = "SELECT * FROM EmailQueue";
        
        try {
            conn = DBUtils.getConnection();
            statement = conn.prepareStatement(query);
            result = statement.executeQuery();
            
            while(result.next()){
                System.out.println(result.getString(2) + "\t" + result.getString(3));
            }
        
        } catch (Exception e) {
            DBUtils.errorHandler("Error while retriving:", e);
        } finally {
            DBUtils.closeConnection();
        }
    }
}
