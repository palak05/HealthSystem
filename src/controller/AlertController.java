package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Utility.ConnectionClass;
import model.Alert;
import model.AlertPatientInfo;
import model.HealthSystemUser;
import model.Observation;
import model.Recommendation;
 

public class AlertController {
	private static final long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;

	public Map<String,Recommendation> getRecommendations(HealthSystemUser user)
	{
        Statement stmt = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        Connection conn = null;
        Map<String,Recommendation> obsMap = new HashMap<String,Recommendation>();
		try
		{
			
		conn = ConnectionClass.connect();
		
		// Create a statement object that will be sending your
		// SQL statements to the DBMS
		stmt = conn.createStatement();
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
		finally {
            ConnectionClass.close(rs1);
            ConnectionClass.close(rs2);
            ConnectionClass.close(rs3);
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
        return obsMap;
	}
	public int insertAlertPatientInfo(String id,int oid,int percentage_threshold, int observation_threshold, int frequency)
	{
        PreparedStatement stmt = null;
        Connection conn = null;
        int result = -1;
		try
		{
			
		conn = ConnectionClass.connect();
		
		String query = "insert into ALERT_PATIENT_INFO values(?, ?, ?,?,?)";

	    stmt = conn.prepareStatement(query); // create a statement
	    stmt.setString(1, id); // set input parameter 1
	    stmt.setInt(2, oid); // set input parameter 2
	    stmt.setInt(3, percentage_threshold); // set input parameter 3
	    stmt.setInt(4, observation_threshold); // set input parameter 4
	    stmt.setInt(5, frequency);
	    
	    result = stmt.executeUpdate(); // execute insert statement
			
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
		finally {
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
		return result;
	}
	
	public List<Alert> generateAlert(HealthSystemUser user)
	{
		try
		{
			UserController uc=new UserController();
			Map<Observation,Recommendation> obsMap = new HashMap<Observation,Recommendation>();
			obsMap=uc.getRecommendations(user);
			for(Map.Entry<Observation, Recommendation> obsRecoValue : obsMap.entrySet())
			{
				Observation observation = obsRecoValue.getKey();
				//TODO Temporary
				observation.setId(5);
				Recommendation recomendation = obsRecoValue.getValue();
				recomendation.setUpperLimit(180.0);
				recomendation.setLowerLimit(120.0);
				AlertPatientInfo alertPatientInfo= getAlertPatientInfo(user,observation);
				
				//Low Frequency
//				Integer frequency = recomendation.getFrequency();
//				if(frequency>0)
//				{
//					Connection conn = null;
//					Statement stmt = null;
//					ResultSet rs = null;
//					try
//					{
//						conn = ConnectionClass.connect();
//						String query = "Select * from Record where patient_id='"+user.getId()+"' AND OBS_ID="+observation.getId()+" AND ROWNUM = 1 ORDER BY OBS_DATE_TIME";
//						stmt = conn.createStatement(); 
//						rs=stmt.executeQuery(query);
//						if(rs.next())
//						{
//							Date date = rs.getDate("OBS_DATE_TIME");
//							java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
//							int difference = (int)((currentDate.getTime() - date.getTime()) / MILLISECONDS_IN_DAY);
//							int alertFrequencyThresold = alertPatientInfo.getAlertFrequencyThreshold();
//							if(difference>(frequency+alertFrequencyThresold))
//								System.out.println("LOW FREQUENCY ALERT !!!!");
//						}
//					}
//					catch(Exception e1)
//					{
//					e1.printStackTrace();
//					}
//					finally
//					{
//						ConnectionClass.close(conn);
//						ConnectionClass.close(stmt);
//						ConnectionClass.close(rs);
//					}
//					
//				}
				
				//Outside the limit
				Double upperLimit = recomendation.getUpperLimit();
				Double lowerLimit = recomendation.getLowerLimit();
				Integer alertObservationThresold = alertPatientInfo.getAlertObservationThreshold();
				Integer alertPercentageThreshold = alertPatientInfo.getAlertPercentageThreshold();
				if(upperLimit!=null&&lowerLimit!=null&&alertObservationThresold!=null&&alertPercentageThreshold!=null)
				{
					Connection conn = null;
					Statement stmt = null;
					ResultSet rs = null;
					try
					{
						conn = ConnectionClass.connect();
						String query = "Select VALUE from (Select DISTINCT VALUE from Record where patient_id='"+user.getId()+"' AND OBS_ID="+observation.getId()+" AND "+alertObservationThresold+" ORDER BY OBS_DATE_TIME DESC) WHERE ROWNUM <= "+alertObservationThresold;
						stmt = conn.createStatement(); 
						rs=stmt.executeQuery(query);
						int count = 0;
						int totalCount = 0;
						while(rs.next())
						{
							String val = rs.getString("VALUE");
							int value = Integer.parseInt(val);
							if(value<lowerLimit || value>upperLimit)
								count ++;
							totalCount++;
						}
						if(totalCount == alertObservationThresold)
						{
							Double percentage = (double) count*100/totalCount;
							if(percentage>alertPercentageThreshold)
								System.out.println("OUTSIDE THE LIMIT ALERT!!");
						}
					}
					catch(Exception e1)
					{
					e1.printStackTrace();
					}
					finally
					{
						ConnectionClass.close(conn);
						ConnectionClass.close(stmt);
						ConnectionClass.close(rs);
					}
					
				}
				
			}
			List<Alert> alertList = new ArrayList<Alert>();
			return alertList;	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public int insertAlert(HealthSystemUser user, Date alert_date)
	{
        PreparedStatement stmt = null,stmt_temp=null,stmt_temps=null,stmt_=null;
        Connection conn = null;
        int result = -1;
		try
		{
			
		conn = ConnectionClass.connect();
		
		String query = "Select * from Record where patient_id='"+user.getId()+"' AND validity=1";
		UserController uc=new UserController();
		Map<Observation,Recommendation> obsMap = new HashMap<Observation,Recommendation>();
		obsMap=uc.getRecommendations(user);
		stmt = conn.prepareStatement(query); 
		ResultSet rs=stmt.executeQuery();
	
		Set<Observation> obse=obsMap.keySet();
		while(rs.next()){
			for (Observation observation : obse) {
				int oid=observation.getId(); 
				if(oid==rs.getInt("OBS_ID"))
				{
					Recommendation rec=obsMap.get(observation);
					if(rec.getLowerLimit()!=null && rec.getUpperLimit()!=null)
					if(rec.getLowerLimit()>rs.getInt("VALUE") || rec.getUpperLimit()<rs.getInt("VALUE"))
					{
						query = "insert into ALERT(ALERT_TYPE,ALERT_STATUS,ALERT_MESSAGE,ALERT_DATE,OBSERVATION_ID,PATIENT_ID,ALERT_SEEN) values(?, ?, ?,?,?,?,?)";
						stmt = conn.prepareStatement(query); // create a statement
						  
					    stmt.setString(1, "outside-the-limit"); // set input parameter 1
					    stmt.setString(2, "ACTIVE"); // set input parameter 2
					    stmt.setString(3, "Observations are not in the prescribed range"); // set input parameter 3
					    stmt.setDate(4, alert_date); // set input parameter 4
					    stmt.setInt(5, oid);
					    stmt.setString(6,user.getId());
					    stmt.setString(7,"false");
					    
					    result = stmt.executeUpdate(); // execute insert statement	
					    query="UPDATE Record SET validity = 0 WHERE record_id="+rec.getId();
					    result = stmt.executeUpdate(query); 
					}
					Date d = alert_date; 
					Date dateBefore = new Date(d.getTime() - rec.getFrequency() * 24 * 3600*1000);
					
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			        java.util.Date parsed = format.parse(dateBefore.toString());
			        java.sql.Date sql = new java.sql.Date(parsed.getTime());
			        
					System.out.print(dateBefore.toString());
					query="SELECT * FROM Record WHERE patient_id='"+user.getId()+"' and obs_date_time between '"+sql+"' and '"+alert_date+"'" ;		
					stmt_temps = conn.prepareStatement(query); // create a statement
					ResultSet rs_temp=stmt_temps.executeQuery();
					if(rs_temp.wasNull())
					{
						query = "insert into ALERT(ALERT_TYPE,ALERT_STATUS,ALERT_MESSAGE,ALERT_DATE,OBSERVATION_ID,PATIENT_ID) values(?, ?, ?,?,?,?)";
						stmt_ = conn.prepareStatement(query); // create a statement
						  
					    stmt_.setString(1, "low-activity"); // set input parameter 1
					    stmt_.setString(2, "ACTIVE"); // set input parameter 2
					    stmt_.setString(3, "Less frequent observations recorded"); // set input parameter 3
					    stmt_.setDate(4, alert_date); // set input parameter 4
					    stmt_.setInt(5, oid);
					    stmt_.setString(6,user.getId());
					    
					    result = stmt.executeUpdate(); // execute insert statement						
					}
				}
			}
		}			
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
		finally {
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
		return result;
	}
	
	public List<Alert> getAllAlerts(HealthSystemUser user)
	{
		List<Alert> alertList = new ArrayList<Alert>();
		
        Statement stmt = null;
        ResultSet rs1 = null;
        Connection conn = null;
		try
		{
			
		conn = ConnectionClass.connect();
		
		// Create a statement object that will be sending your
		// SQL statements to the DBMS
		stmt = conn.createStatement();

		//Alert
		rs1 = stmt.executeQuery("SELECT a.ALERT_ID,a.ALERT_TYPE,a.ALERT_STATUS,a.ALERT_MESSAGE,a.ALERT_DATE,"
				+" o.OBSERVATION_ID, o.OBSERVATION_TYPE, o.MEASURE, o. METRIC, o.DESCRIPTION "
			+ "FROM ALERT a, OBSERVATION o where a.ALERT_STATUS = 'ACTIVE' and a.ALERT_SEEN = 'false' and a.OBSERVATION_ID=o.OBSERVATION_ID and a.PATIENT_ID = '"+user.getId()+"'");
		
		while (rs1.next()) {
			//Add disease
			Observation observation = new Observation(rs1.getInt("OBSERVATION_ID"), rs1.getString("OBSERVATION_TYPE"),rs1.getString("DESCRIPTION"),rs1.getString("MEASURE"), rs1.getString("METRIC"));
			Alert alert = new Alert(rs1.getString("ALERT_ID"),rs1.getString("ALERT_TYPE"),rs1.getString("ALERT_STATUS"),rs1.getString("ALERT_MESSAGE"),rs1.getString("ALERT_DATE"),observation);
			alertList.add(alert);
		}

        } catch(Throwable oops) {
            oops.printStackTrace();
        }
		
		finally {
            ConnectionClass.close(rs1);
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
        return alertList;
	}
	
	public List<Alert> getAllAlertsForHealthSupporter(HealthSystemUser user)
	{
		List<Alert> alertList = new ArrayList<Alert>();
		
        Statement stmt = null;
        ResultSet rs1 = null;
        Connection conn = null;
		try
		{
			
		conn = ConnectionClass.connect();
		
		// Create a statement object that will be sending your
		// SQL statements to the DBMS
		stmt = conn.createStatement();

		//Alert
			rs1 = stmt.executeQuery("SELECT a.ALERT_ID,a.ALERT_TYPE,a.ALERT_STATUS,a.ALERT_MESSAGE,a.ALERT_DATE,"
					+" o.OBSERVATION_ID, o.OBSERVATION_TYPE, o.MEASURE, o. METRIC, o.DESCRIPTION, "
					+" h.NAME, h.ADDRESS, h.GENDER, h.DOB, h.PASSWORD, h.TYPE "
				+ "FROM ALERT a, OBSERVATION o, HEALTHSYSTEM_USER h where a.PATIENT_ID = a.ID and a.ALERT_STATUS = 'ACTIVE' and a.ALERT_SEEN = 'false' and a.OBSERVATION_ID=o.OBSERVATION_ID and a.PATIENT_ID IN "
				+ "(Select PATIENT_ID from AUTHORIZATION where HEALTH_SUPPORTER_ID = '"+user.getId()+"')");
			
			while (rs1.next()) {
				//Add disease
				Observation observation = new Observation(rs1.getInt("OBSERVATION_ID"), rs1.getString("OBSERVATION_TYPE"),rs1.getString("DESCRIPTION"),rs1.getString("MEASURE"), rs1.getString("METRIC"));
				HealthSystemUser patient = new HealthSystemUser(rs1.getString("ID"), rs1.getDate("DOB"), rs1.getString("GENDER"), rs1.getString("ADDRESS"), rs1.getString("NAME"), rs1.getString("TYPE"));
				Alert alert = new Alert(rs1.getString("ALERT_ID"),rs1.getString("ALERT_TYPE"),rs1.getString("ALERT_STATUS"),rs1.getString("ALERT_MESSAGE"),rs1.getString("ALERT_DATE"),observation,patient);
				alertList.add(alert);
			}

        } catch(Throwable oops) {
            oops.printStackTrace();
        }
		
		finally {
            ConnectionClass.close(rs1);
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
        return alertList;
	}
	
	public AlertPatientInfo getAlertPatientInfo(HealthSystemUser user)
	{
		AlertPatientInfo alertPatientInfo = null;
        Statement stmt = null;
        ResultSet rs1 = null;
        Connection conn = null;
		try
		{
			
		conn = ConnectionClass.connect();
		
		// Create a statement object that will be sending your
		// SQL statements to the DBMS
		stmt = conn.createStatement();

		String sql = "SELECT * "
				+ "FROM ALERT_PATIENT_INFO a, OBSERVATION o where a.PATIENT_ID = '"+user.getId()+"' and o.OBSERVATION_ID=a.OBSERVATION_ID";
			//Login
			rs1 = stmt.executeQuery(sql);
			
			if(rs1.next()) {
				//Add user details
				Observation observation = new Observation(rs1.getInt("OBSERVATION_ID"), rs1.getString("OBSERVATION_TYPE"), rs1.getString("DESCRIPTION"),rs1.getString("MEASURE"),rs1.getString("METRIC"));
				alertPatientInfo = new AlertPatientInfo();
				alertPatientInfo.setPatient(user);
				alertPatientInfo.setObservation(observation);
				alertPatientInfo.setAlertFrequencyThreshold(rs1.getInt("ALERT_PERCENTAGE_THRESHOLD"));
				alertPatientInfo.setAlertPercentageThreshold(rs1.getInt("ALERT_OBS_THRESHOLD"));
				alertPatientInfo.setAlertFrequencyThreshold(rs1.getInt("ALERT_FREQUENCY_THRESHOLD"));
			}

        } catch(Throwable oops) {
            oops.printStackTrace();
        }
		finally {
            ConnectionClass.close(rs1);
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
		return alertPatientInfo;
	}
	
	public AlertPatientInfo getAlertPatientInfo(HealthSystemUser user,Observation observation)
	{
		AlertPatientInfo alertPatientInfo = null;
        Statement stmt = null;
        ResultSet rs1 = null;
        Connection conn = null;
		try
		{
			
		conn = ConnectionClass.connect();
		
		// Create a statement object that will be sending your
		// SQL statements to the DBMS
		stmt = conn.createStatement();

		String sql = "SELECT * "
				+ "FROM ALERT_PATIENT_INFO where PATIENT_ID = '"+user.getId()+"' and OBSERVATION_ID="+observation.getId();
			//Login
			rs1 = stmt.executeQuery(sql);
			
			if(rs1.next()) {
				//Add user details
				alertPatientInfo = new AlertPatientInfo();
				alertPatientInfo.setPatient(user);
				alertPatientInfo.setObservation(observation);
				alertPatientInfo.setAlertPercentageThreshold(rs1.getInt("ALERT_PERCENTAGE_THRESHOLD"));
				alertPatientInfo.setAlertObservationThreshold(rs1.getInt("ALERT_OBS_THRESHOLD"));
				alertPatientInfo.setAlertFrequencyThreshold(rs1.getInt("ALERT_FREQUENCY_THRESHOLD"));
			}

        } catch(Throwable oops) {
            oops.printStackTrace();
            alertPatientInfo = new AlertPatientInfo();
            HealthSystemUser user1 = new HealthSystemUser();
            user1.setId("P2");
            Observation observation1 = new Observation();
            observation.setId(5);
            alertPatientInfo.setPatient(user);
            alertPatientInfo.setObservation(observation1);
            alertPatientInfo.setAlertObservationThreshold(2);
            alertPatientInfo.setAlertPercentageThreshold(50);
        }
		finally {
            ConnectionClass.close(rs1);
            ConnectionClass.close(stmt);
            ConnectionClass.close(conn);
        }
		return alertPatientInfo;
	}
	
//	void alertSeen(Alert alert)
//	{
//		
//	}
	
	
	

}