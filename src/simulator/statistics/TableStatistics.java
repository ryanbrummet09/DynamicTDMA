package simulator.statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import constants.TableStatisticsIndexConstant;

/**
 * Used to build a csv file of statistics for a single simulation run.  
 * Use with TableStatisticsSimulator
 * @author ryanbrummet
 *
 */
public class TableStatistics {
	
	private final List<int[]> results;
	private int[] instance;
	
	public TableStatistics() {
		results = new ArrayList<int[]>();
		instance = new int[TableStatisticsIndexConstant.COLUMN_NAMES.length];
	}
	
	public void assign(int index, int value) {
		instance[index] = value;
	}
	
	public void next() {
		results.add(instance);
		instance = new int[TableStatisticsIndexConstant.COLUMN_NAMES.length];
	}
	
	/**
	 * print information from simulation associated with the object to file (all locations of packets and each nodes status)
	 * @param fileName
	 * @throws IOException
	 */
	public void saveResultsToFile(String fileName) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + ".csv"));
		for(int i = 0; i < TableStatisticsIndexConstant.COLUMN_NAMES.length; i++) {
			if(i + 1 == TableStatisticsIndexConstant.COLUMN_NAMES.length) {
				bw.write(TableStatisticsIndexConstant.COLUMN_NAMES[i]);
			} else {
				bw.write(TableStatisticsIndexConstant.COLUMN_NAMES[i] + ",");
			}
		}
		bw.newLine();
		
		for(int[] line : results) {
			for(int i = 0; i < TableStatisticsIndexConstant.COLUMN_NAMES.length; i ++) {
				if(i + 1 == TableStatisticsIndexConstant.COLUMN_NAMES.length) {
					bw.write(Integer.toString(line[i]));
				} else {
					bw.write(Integer.toString(line[i]) + ",");
				}
			}
			bw.newLine();
		}
    	bw.close();
	}
}
