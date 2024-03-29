import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class GUI extends JFrame {

	private static Connection conn = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;
	private static ResultSet resultSetName = null;
	private static Object updateSet = null;
	public static JLabel stockName = new JLabel();
	public static JComboBox stockBox = new JComboBox();
	public static String symbol = null;
	public static String name= null;
	public static String dailyChange = null;
	public static String volume = null;
	public static String dailyHigh = null;
	public static String dailyLow = null;
	public static String lastTradeTime = null;
	public static String lastTradeDate = null;
	public static String lastTradeValue = null;
	public static String opening = null;
	private JPanel contentPane;


	public static void main(String[] args) {
		String csvDir = System.getProperty("user.dir") + "\\stocks.csv";
		System.out.println(csvDir);
		String URL = "http://download.finance.yahoo.com/d/quotes.csv?s=";
		try {
			conn = Connect.Connect();
			statement = conn.createStatement();
			resultSet = statement.executeQuery("SELECT SYMBOL FROM `watchedstocks`");
			while (resultSet.next()){
				if (resultSet.isLast()) {
					URL += resultSet.getObject("Symbol").toString();
				}
				else {
					URL += resultSet.getObject("Symbol").toString() + ",";
				}
			}
			conn.close();
			
		} catch (SQLException e1) {
			e1.printStackTrace();
			URL += "AAPL,IBM,GS,AXP,USB,DE,WFC,WMT,KO,PG";
		}
		URL+= "&f=snd1t1l1c1ohgv&e=.csv";
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(new URL(URL), new File(csvDir));
		} catch (Exception x) { x.printStackTrace(); }
		System.out.println("Successfully downloaded Yahoo Finance Stock Data!");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUI() {
		setTitle("Stock Inspector (simple view)");
		conn = Connect.Connect();
		try {
			Initialize();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void Initialize() throws FileNotFoundException{

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		stockBox.setMaximumRowCount(10);
		stockBox.setBounds(103, 41, 100, 25);
		contentPane.add(stockBox);

		stockName.setBounds(108, 72, 204, 14);
		contentPane.add(stockName);

		final JLabel lblSelectStock = new JLabel("Select Stock:");
		lblSelectStock.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSelectStock.setBounds(10, 42, 93, 20);
		contentPane.add(lblSelectStock);

		final JLabel lblStockCompany = new JLabel("Stock Company:");
		lblStockCompany.setBounds(10, 72, 108, 14);
		contentPane.add(lblStockCompany);

		final JLabel lblShareValue = new JLabel("Share Value:");
		lblShareValue.setBounds(10, 98, 76, 14);
		contentPane.add(lblShareValue);

		final JLabel stockLastTradeValue = new JLabel("");
		stockLastTradeValue.setBounds(86, 98, 46, 14);
		contentPane.add(stockLastTradeValue);

		final JLabel lblStockInspector = new JLabel("Stock Inspector");
		lblStockInspector.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblStockInspector.setBounds(5, 2, 257, 31);
		contentPane.add(lblStockInspector);

		final JLabel lblDailyHigh = new JLabel("Daily High:");
		lblDailyHigh.setBounds(10, 192, 60, 14);
		contentPane.add(lblDailyHigh);

		final JLabel lblDailyLow = new JLabel("Daily Low:");
		lblDailyLow.setBounds(10, 215, 60, 14);
		contentPane.add(lblDailyLow);

		final JLabel stockDailyHigh = new JLabel("");
		stockDailyHigh.setBounds(75, 192, 60, 14);
		contentPane.add(stockDailyHigh);

		final JLabel stockDailyLow = new JLabel("");
		stockDailyLow.setBounds(75, 215, 60, 14);
		contentPane.add(stockDailyLow);

		final JLabel lblLastTradeDaytime = new JLabel("Last Trade:");
		lblLastTradeDaytime.setBounds(10, 122, 65, 14);
		contentPane.add(lblLastTradeDaytime);

		final JLabel stockLastTradeDate = new JLabel("");
		stockLastTradeDate.setBounds(82, 122, 70, 14);
		contentPane.add(stockLastTradeDate);

		final JLabel lblDailyChange = new JLabel("Daily Change:");
		lblDailyChange.setBounds(10, 146, 76, 14);
		contentPane.add(lblDailyChange);

		final JLabel stockDailyChange = new JLabel("");
		stockDailyChange.setBounds(92, 146, 46, 14);
		contentPane.add(stockDailyChange);

		final JLabel stockLastTradeTime = new JLabel("");
		stockLastTradeTime.setBounds(150, 122, 76, 14);
		contentPane.add(stockLastTradeTime);

		final JLabel lblOpening = new JLabel("Opening:");
		lblOpening.setBounds(10, 170, 54, 14);
		contentPane.add(lblOpening);

		final JLabel stockOpening = new JLabel("");
		stockOpening.setBounds(65, 170, 70, 14);
		contentPane.add(stockOpening);

		final JLabel lblVolume = new JLabel("Volume:");
		lblVolume.setBounds(10, 236, 54, 14);
		contentPane.add(lblVolume);

		final JLabel stockVolume = new JLabel("");
		stockVolume.setBounds(62, 236, 83, 14);
		contentPane.add(stockVolume);

		JLabel lblShares = new JLabel("Shares");
		lblShares.setBounds(120, 236, 46, 14);
		contentPane.add(lblShares);

		JButton btnNewButton = new JButton("Value Chart");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChartValue.main(null);
			}
		});
		btnNewButton.setBounds(10, 261, 108, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Volume Chart");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChartVolume.main(null);
			}
		});
		btnNewButton_1.setBounds(130, 261, 132, 23);
		contentPane.add(btnNewButton_1);

		JButton btnStockToStock = new JButton("Compare Stocks..");
		btnStockToStock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StockComparisons sc = new StockComparisons();
				sc.setVisible(true);
				StockComparisons.stockBoxA.removeAllItems();
				StockComparisons.stockBoxB.removeAllItems();
				for(int i = 0; i < GUI.stockBox.getItemCount(); i++){
					StockComparisons.stockBoxA.addItem(GUI.stockBox.getItemAt(i));
					StockComparisons.stockBoxB.addItem(GUI.stockBox.getItemAt(i));
				}
			}
		});
		btnStockToStock.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnStockToStock.setBounds(44, 295, 159, 23);
		contentPane.add(btnStockToStock);

		stockBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = stockBox.getSelectedItem().toString();
				try {
					statement = conn.createStatement();
					resultSet = statement
							.executeQuery("SELECT * FROM " + name + "");
					writeLabels(resultSet);	 
				}catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					statement = conn.createStatement();
					resultSetName = statement
							.executeQuery("SELECT NAME FROM `watchedstocks` WHERE SYMBOL = '"  + name + "'");
					writeName(resultSetName);	 
				}catch (SQLException e) {
					e.printStackTrace();
				}

			}

			private void writeLabels(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					lastTradeDate = resultSet.getObject("Last_Trade_DateTime").toString().split(", ")[0];
					lastTradeTime = resultSet.getObject("Last_Trade_DateTime").toString().split(", ")[1];
					lastTradeValue =  resultSet.getObject("Last_Trade_Value").toString();
					dailyChange = resultSet.getObject("Daily_Change").toString();
					opening = resultSet.getObject("Opening").toString();
					dailyHigh = resultSet.getObject("Daily_High").toString();
					dailyLow = resultSet.getObject("Daily_Low").toString();
					volume = resultSet.getObject("Volume").toString();
					stockLastTradeValue.setText(lastTradeValue);
					stockLastTradeDate.setText(lastTradeDate);
					stockLastTradeTime.setText(lastTradeTime);
					stockDailyChange.setText(dailyChange);
					stockOpening.setText(opening);
					stockDailyHigh.setText(dailyHigh);
					stockDailyLow.setText(dailyLow);
					stockVolume.setText(volume);
				}
			}


			private void writeName(ResultSet resultSet) throws SQLException {
				while (resultSet.next()) {
					name = resultSetName.getObject("Name").toString();
					stockName.setText(name);
				}
			}

		});


		try {
			statement = conn.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS `watchedstocks` "
					+ "(`Symbol` varchar(4) NOT NULL PRIMARY KEY, `Name` varchar(31))");
		}catch (SQLException e) {
			e.printStackTrace();
		}



		String csvDir = System.getProperty("user.dir") + "\\stocks.csv";
		InputStream inputStream = new FileInputStream(csvDir);
		InputStreamReader is = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(is); 
		try{
			String line = br.readLine(); 
			while (line != null){
				String[] stockinfo1 = line.split("\"");
				String[] stockinfo2 = stockinfo1[8].split(",");
				String[] date = stockinfo1[5].split("/");
				String datetime = date[2] + "/" + date[0] + "/" + date[1] + ", " + stockinfo1[7];
				String query1 = "CREATE TABLE IF NOT EXISTS `" + stockinfo1[1] + "` ("
						+ "`Last_Trade_DateTime` varchar(25) NOT NULL PRIMARY KEY, `Last_Trade_Value` decimal(5,2), "
						+ "`Daily_Change` decimal(4,2), `Opening` decimal(5,2), `Daily_High` decimal(5,2), "
						+ "`Daily_Low` decimal(5,2), `Volume` int(8)" + ")";
				String query2 = "INSERT INTO `watchedstocks` (Symbol, Name) VALUES ('" + stockinfo1[1]
						+ "', '" + stockinfo1[3] + "')";
				String query3 = "INSERT INTO `" + stockinfo1[1] + "` (Last_Trade_DateTime, "
						+ "Last_Trade_Value, Daily_Change, Opening, Daily_High, Daily_Low, Volume) "
						+ "VALUES ('" + datetime + "', '" + stockinfo2[1] + "', '" + stockinfo2[2] 
								+ "', '" + stockinfo2[3] + "', '" + stockinfo2[4] + "', '" + stockinfo2[5]
										+ "', '" + stockinfo2[6] + "')";
				try{
					statement = conn.createStatement();
					statement.executeUpdate(query1);
					try{
						statement.executeUpdate(query2);
					} catch(MySQLIntegrityConstraintViolationException e){
						e.printStackTrace();
					}
					statement.executeUpdate(query3);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				line = br.readLine();
			}
			br.close();

		} catch (IOException e) {
			Logger log = Logger.getLogger(GUI.class.getName()); 
			log.log(Level.SEVERE, e.toString(), e);
		}

		try {
			statement = conn.createStatement();
			resultSet = statement
					.executeQuery("SELECT * FROM watchedstocks");
			populateComboBox(resultSet);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void populateComboBox(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		while (resultSet.next()) {
			stockBox.addItem(resultSet.getObject("Symbol").toString());

		}

	}
}
