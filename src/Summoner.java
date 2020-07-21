import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Summoner {

	// Instance Variables
	private String api_key;
	private String summonerName;
	
	// General Account Information
	private String id;
	private String accountId;
	private String puuid;
	private int profileIconId;
	private int summonerLevel;
	
	// Ranked Information
	private String leagueIdSolo;
	private String tierSolo;
	private int divisionSolo;
	private int leaguePointsSolo;
	private int winsSolo;
	private int lossesSolo;
	
	private String leagueIdFlex;
	private String tierFlex;
	private int divisionFlex;
	private int leaguePointsFlex;
	private int winsFlex;
	private int lossesFlex;
	
	// Match History
	private ArrayList<Match> matchHistory;
	
	public Summoner(String sumName, String key) {
		api_key = key;
		summonerName = sumName;
		
		// Using methods to instantiate other instance variables
		getSummonerInfo();
		getRankedInfo();
		
		matchHistory = new ArrayList<Match>();
		instantiateMatchHistory();
	}
	
	
	/**
	 * This method collects the id, accountId, puuid, profileIconId, and
	 * summonerLevel of the given summoner and assigns values to instance variables accordingly
	 */
	private void getSummonerInfo() {
		try{
			URL u = new URL("https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + api_key);
			HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
			
			if(urlConnection.getResponseCode() == 200) {
				InputStream is = urlConnection.getInputStream();
				Scanner s = new Scanner(is);
				
				// Reading all information from line and removing the brackets
				String readInfo = s.nextLine();
				String cleanInfo = readInfo.substring(1, readInfo.length() - 1);

				// Unused info: info[3] = summonerName and info[5] = revisionDate
				String[] info = cleanInfo.split(",");
				
				// Parsing info and assigning to respective instance variables
				id = info[0].substring(info[0].indexOf(":") + 2, info[0].length() - 1);
				accountId = info[1].substring(info[1].indexOf(":") + 2, info[1].length() - 1);
				puuid = info[2].substring(info[2].indexOf(":") + 2, info[2].length() - 1);
				profileIconId = Integer.parseInt(info[4].substring(info[4].indexOf(":") + 1));
				summonerLevel = Integer.parseInt(info[6].substring(info[6].indexOf(":") + 1));
				
				/*
				System.out.println("id = " + id);
				System.out.println("accountId = " + accountId);
				System.out.println("puuid = " + puuid);
				System.out.println("profileIconId = " + profileIconId);
				System.out.println("summonerLevel = " + summonerLevel);
				*/
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	
	/**
	 * This method uses the summonerid (id) to gather and assign information of the summoners
	 * ranked queues
	 */
	private void getRankedInfo() {
		try {
			URL u = new URL("https://na1.api.riotgames.com/lol/league/v4/entries/by-summoner/" + id + "?api_key=" + api_key);
			HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
			
			if(urlConnection.getResponseCode() == 200) {
				InputStream is = urlConnection.getInputStream();
				Scanner s = new Scanner(is).useDelimiter("},");
				
				// Separating ranked solo info from ranked flex info
				// Unused info: [1] = queueType, [4] = summonerId, [5] = summonerName, [9] = veteran,
				//				[10] = inactive, [11] = freshBlood, [12] = hotStreak
				String solo = s.next();
				String flex = s.next();
				String[] soloInfo = solo.split(",");
				String[] flexInfo = flex.split(",");
				
				// Parsing and assigning values to respective variables
				leagueIdSolo = soloInfo[0].substring(soloInfo[0].indexOf(":") + 2, soloInfo[0].length() - 1);
				tierSolo = soloInfo[2].substring(soloInfo[2].indexOf(":") + 2, soloInfo[2].length() - 1);
				divisionSolo = parseDivision(soloInfo[3].substring(soloInfo[3].indexOf(":") + 2, soloInfo[3].length() - 1));
				leaguePointsSolo = Integer.parseInt(soloInfo[6].substring(soloInfo[6].indexOf(":") + 1));
				winsSolo = Integer.parseInt(soloInfo[7].substring(soloInfo[7].indexOf(":") + 1));
				lossesSolo = Integer.parseInt(soloInfo[8].substring(soloInfo[8].indexOf(":") + 1));
				
				leagueIdFlex = flexInfo[0].substring(flexInfo[0].indexOf(":") + 2, flexInfo[0].length() - 1);
				tierFlex = flexInfo[2].substring(flexInfo[2].indexOf(":") + 2, flexInfo[2].length() - 1);
				divisionFlex = parseDivision(flexInfo[3].substring(flexInfo[3].indexOf(":") + 2, flexInfo[3].length() - 1));
				leaguePointsFlex = Integer.parseInt(flexInfo[6].substring(flexInfo[6].indexOf(":") + 1));
				winsFlex = Integer.parseInt(flexInfo[7].substring(flexInfo[7].indexOf(":") + 1));
				lossesFlex = Integer.parseInt(flexInfo[8].substring(flexInfo[8].indexOf(":") + 1));
				
				/*
				System.out.println("leagueIdSolo: " + leagueIdSolo);
				System.out.println("tierSolo: " + tierSolo);
				System.out.println("divisionSolo: " + divisionSolo);
				System.out.println("leaguePointsSolo: " + leaguePointsSolo);
				System.out.println("winsSolo: " + winsSolo);
				System.out.println("lossesSolo: " + lossesSolo);
				
				System.out.println("==============================================================================");
				
				System.out.println("leagueIdFlex: " + leagueIdFlex);
				System.out.println("tierFlex: " + tierFlex);
				System.out.println("divisionFlex: " + divisionFlex);
				System.out.println("leaguePointsFlex: " + leaguePointsFlex);
				System.out.println("winsFlex: " + winsFlex);
				System.out.println("lossesFlex: " + lossesFlex);
				*/
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	
	/**
	 * This method converts the division given in roman numerals to an integer
	 * @param division - the division given in roman numerals
	 * @return - the division as an integer
	 */
	private int parseDivision(String division) {
		if(division.equals("IV")) {
			return 4;
		}else if(division.equals("III")) {
			return 3;
		}else if(division.equals("II")) {
			return 2;
		}else {
			return 1;
		}
	}

	
	/**
	 * This method uses the accountId (accountId) to add all matches found to the 
	 * matchHistory ArrayList
	 */
	private void instantiateMatchHistory() {
		try {
			URL u = new URL("https://na1.api.riotgames.com/lol/match/v4/matchlists/by-account/" + accountId + "?api_key=" + api_key);
			HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
			
			if(urlConnection.getResponseCode() == 200) {
				InputStream is = urlConnection.getInputStream();
				Scanner s = new Scanner(is);
				
				// Getting all match data and splitting into individual matches
				String allMatches = s.nextLine();
				String[] matches = allMatches.split("},");
				
				// Parsing all gameIds from matches[], creating a Match object for each gameId, and adding it to matchHistory
				for(String str : matches) {
					long gameId = Long.parseLong(str.substring(str.indexOf("gameId") + 8, str.indexOf("champion") - 2));
					Match m = new Match(gameId, api_key);
					matchHistory.add(m);
				}
				System.out.println(matchHistory.size());
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
