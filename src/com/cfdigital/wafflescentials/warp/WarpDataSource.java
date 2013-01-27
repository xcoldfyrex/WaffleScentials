package com.cfdigital.wafflescentials.warp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.cfdigital.wafflescentials.util.ConnectionManager;
import com.cfdigital.wafflescentials.util.WaffleLogger;


public class WarpDataSource {
    private final static String WARP_TABLE = "CREATE TABLE `warpTable` ("
	    + "`id` INTEGER PRIMARY KEY,"
	    + "`name` varchar(32) NOT NULL DEFAULT 'warp',"
        + "`creator` varchar(32) NOT NULL DEFAULT 'Player',"
        + "`world` varchar(32) NOT NULL DEFAULT '0',"
        + "`x` DOUBLE NOT NULL DEFAULT '0',"
        + "`y` tinyint NOT NULL DEFAULT '0',"
        + "`z` DOUBLE NOT NULL DEFAULT '0',"
        + "`yaw` smallint NOT NULL DEFAULT '0',"
        + "`pitch` smallint NOT NULL DEFAULT '0',"
        + "`publicAll` boolean NOT NULL DEFAULT '1',"
        + "`permissions` text,"
        + "`welcomeMessage` varchar(100) NOT NULL DEFAULT ''"
        + ");";

    public static void initialize() {
        if (!tableExists()) {
            createTable();
        }
        dbTblCheck();
    }

    public static HashMap<String, Warp> getMap() {
        HashMap<String, Warp> ret = new HashMap<String, Warp>();
        Statement statement = null;
        ResultSet set = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            statement = conn.createStatement();
            set = statement.executeQuery("SELECT * FROM warpTable");
            int size = 0;
            while (set.next()) {
                size++;
                int index = set.getInt("id");
                String name = set.getString("name");
                String creator = set.getString("creator");
                String world = set.getString("world");
                double x = set.getDouble("x");
                int y = set.getInt("y");
                double z = set.getDouble("z");
                int yaw = set.getInt("yaw");
                int pitch = set.getInt("pitch");
                boolean publicAll = set.getBoolean("publicAll");
                String permissions = set.getString("permissions");
                String welcomeMessage = set.getString("welcomeMessage");
                Warp warp = new Warp(index, name, creator, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage);
                ret.put(name, warp);
            }
            WaffleLogger.info(" " + size + " warps loaded");
        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Load Exception");
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (set != null)
                    set.close();
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Load Exception (on close)");
            }
        }
        return ret;
    }

    private static boolean tableExists() {
        ResultSet rs = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, "warpTable", null);
            if (!rs.next())
                return false;
            return true;
        } catch (SQLException ex) {
        	WaffleLogger.severe("Table Check Exception", ex);
            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
            	WaffleLogger.severe("Table Check SQL Exception (on closing)");
            }
        }
    }

    private static void createTable() {
    	Statement st = null;
    	try {
    		WaffleLogger.info("Creating Database...");
    		Connection conn = ConnectionManager.getConnection();
    		st = conn.createStatement();
    		st.executeUpdate(WARP_TABLE);
    		conn.commit();
    		
    		if(WarpSettings.usemySQL){ 
    			// We need to set auto increment on SQL.
    			String sql = "ALTER TABLE `warpTable` CHANGE `id` `id` INT NOT NULL AUTO_INCREMENT ";
    			WaffleLogger.info("Modifying database for MySQL support");
    			st = conn.createStatement();
    			st.executeUpdate(sql);
    			conn.commit();
    			
    			// Check for old warps.db and import to mysql
    			
    		}
    	} catch (SQLException e) {
    		WaffleLogger.severe("Create Table Exception", e);
    	} finally {
    		try {
    			if (st != null) {
    				st.close();
    			}
    		} catch (SQLException e) {
    			WaffleLogger.severe("Could not create the table (on close)");
    		}
    	}
    }

    public static void moveWarp(Warp warp) {
        PreparedStatement ps = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn
                    .prepareStatement("UPDATE warpTable SET x=?, y=?, z=?, yaw=?, pitch=? WHERE id=?");
            ps.setDouble(1, warp.x);
            ps.setDouble(2, warp.y);
            ps.setDouble(3, warp.z);
            ps.setInt(4, warp.yaw);
            ps.setInt(5, warp.pitch);
            ps.setInt(6, warp.index);

            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Update Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Update Exception (on close)", ex);
            }
        }
    }
    
    public static void addWarp(Warp warp) {
        PreparedStatement ps = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn
                    .prepareStatement("INSERT INTO warpTable (id, name, creator, world, x, y, z, yaw, pitch, publicAll, permissions, welcomeMessage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, warp.index);
            ps.setString(2, warp.name);
            ps.setString(3, warp.creator);
            ps.setString(4, warp.world);
            ps.setDouble(5, warp.x);
            ps.setDouble(6, warp.y);
            ps.setDouble(7, warp.z);
            ps.setInt(8, warp.yaw);
            ps.setInt(9, warp.pitch);
            ps.setBoolean(10, warp.publicAll);
            ps.setString(11, warp.permissionsString());
            ps.setString(12, warp.welcomeMessage);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Insert Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Insert Exception (on close)", ex);
            }
        }
    }

    public static void deleteWarp(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("DELETE FROM warpTable WHERE id = ?");
            ps.setInt(1, warp.index);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Delete Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Delete Exception (on close)", ex);
            }
        }
    }

    public static void publicizeWarp(Warp warp, boolean publicAll) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET publicAll = ? WHERE id = ?");
            ps.setBoolean(1, publicAll);
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Publicize Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Publicize Exception (on close)", ex);
            }
        }
    }

    public static void updatePermissions(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET permissions = ? WHERE id = ?");
            ps.setString(1, warp.permissionsString());
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Permissions Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Permissions Exception (on close)", ex);
            }
        }
    }

    public static void updateCreator(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET creator = ? WHERE id = ?");
            ps.setString(1, warp.creator);
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Creator Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Creator Exception (on close)", ex);
            }
        }
    }

    public static void updateWelcomeMessage(Warp warp) {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("UPDATE warpTable SET welcomeMessage = ? WHERE id = ?");
            ps.setString(1, warp.welcomeMessage);
            ps.setInt(2, warp.index);
            ps.executeUpdate();
            conn.commit();

        } catch (SQLException ex) {
        	WaffleLogger.severe("Warp Creator Exception", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	WaffleLogger.severe("Warp Creator Exception (on close)", ex);
            }
        }
    }
    
    public static void dbTblCheck() {
    	// Add future modifications to the table structure here
    	
    }

    public static void updateDB(String test, String sql) {
    	// Use same sql for both mysql/sqlite
    	updateDB(test, sql, sql);
    }

    public static void updateDB(String test, String sqlite, String mysql) {
    	// Allowing for differences in the SQL statements for mysql/sqlite.
    	try {
    		Connection conn = ConnectionManager.getConnection();
    		Statement statement = conn.createStatement();
    		statement.executeQuery(test);
    		statement.close();
    	} catch(SQLException ex) {
    		WaffleLogger.info("Updating database");
    		// Failed the test so we need to execute the updates
    		try {
    			String[] query;
    			if (WarpSettings.usemySQL) {
    				query = mysql.split(";");
    			} else { 
    				query = sqlite.split(";");
    			}

    			Connection conn = ConnectionManager.getConnection();
    			Statement sqlst = conn.createStatement();
    			for (String qry : query) {
    				sqlst.executeUpdate(qry);
    			}
    			conn.commit();
    			sqlst.close();
    		} catch (SQLException exc) {
    			WaffleLogger.severe("Failed to update the database to the new version - ", exc);
    			ex.printStackTrace();
    		}	
    	}
    }

    public static void updateFieldType(String field, String type) {
    	try {
    		if (!WarpSettings.usemySQL) return;
    		WaffleLogger.info("Updating database");
    		
    		Connection conn = ConnectionManager.getConnection();
    		DatabaseMetaData meta = conn.getMetaData();

    		ResultSet colRS = null;
    		colRS = meta.getColumns(null, null, "warpTable", null);
    		while (colRS.next()) {
    			String colName = colRS.getString("COLUMN_NAME");
    			String colType = colRS.getString("TYPE_NAME");
    			
    			if (colName.equals(field) && !colType.equals(type))
    			{
    				Statement stm = conn.createStatement();
    				stm.executeUpdate("ALTER TABLE warpTable MODIFY " + field + " " + type + "; ");
    				conn.commit();
    				stm.close();
    				break;
    			}
    		}
    		colRS.close();
    	} catch(SQLException ex) {
    		WaffleLogger.severe("Failed to update the database to the new version - ", ex);
    		ex.printStackTrace();
    	}
    }

}
