package catfuse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Main 
{
	public static String MISSING_VALUE = "999";
	
	
	public static void main( String[] args ) 
	{
		// Declare parameters for arguments
		String infilename = null;
		double min = 0;

		// VERBOSE
		if( args.length < 2 )
		{
			System.out.println( "FEHLER: Es muss eine Eingabedatei und eine Untergrenze angegeben werden!" );
			System.exit( 1 );
		}
		
		// Get parameters
		infilename = args[ 0 ];
		min = Double.parseDouble( args[ 1 ] );
		
//		// TEST
//		infilename = "test.dat";
//		min = 0.05;

		
		// Create output filename
		String outfilename = infilename.split( "\\." )[ 0 ] + "_fused." +  infilename.split( "\\." )[ 1 ];

		// VERBOSE
		System.out.println( "Verarbeite Datei '" + infilename + "' mit < " + min + ":" );
		System.out.println();
		
		
		// VERBOSE
		System.out.println( "Datei einlesen..." );
		
		List<List<String>> datatable = new LinkedList<List<String>>(); 
		try 
		{
			BufferedReader reader = new BufferedReader( new FileReader( infilename ) );

		    // Go through every line
			String line = null;
			do
			{
				line = reader.readLine();	
				if( line != null )
				{
					String[] items = line.split( "\t" );
					for( int i = 0; i < items.length; i++ )
					{
						items[ i ] = items[ i ].trim();
					}
					List<String> row = Arrays.asList( items );
					datatable.add( row );
				}
			}
		    while( line != null ); 
		    
		    reader.close();
		}
		catch( FileNotFoundException e ) 
		{
			System.out.println( "FEHLER: Eingabedatei " + infilename + " wurde nicht gefunden!" );
			System.exit( 1 );
		} 
		catch( IOException e ) 
		{
			System.out.println( "FEHLER: Lesen der Eingabedatei " + infilename + " nicht mögich!" );
			System.exit( 1 );
		}


		// Calculate found items
		int noOfItems = 0;
		int noOfRows = 0;
		if( !datatable.isEmpty() )
		{
			noOfRows = datatable.size();
			noOfItems = datatable.get( 0 ).size();
		}
		else
		{
			System.out.println( "FEHLER: Eingabedatei " + infilename + " ist leer." );
			System.exit( 1 );
		}
		
		
		// VERBOSE
		System.out.println( "Beginne Zusammenlegung: " + noOfRows + " Datenzeilen mit " + noOfItems + " Items werden verarbeitet..." );
		System.out.println();

		for( int i = 0; i < noOfItems; i++ )
		{
			boolean isFusion = true;
			while( isFusion )
			{
				HashMap<String, Double> relFreq = new HashMap<String, Double>();
	
				// Count absolute frequencies
				for( List<String> row : datatable )
				{
					if( !relFreq.containsKey( row.get( i ) ) )
					{
						relFreq.put( row.get( i ), 0.0 );
					}
					relFreq.put( row.get( i ), relFreq.get( row.get( i ) ) + 1 );
				}
				
				// Normalize to relative frequencies
				double noOfMissingVals = 0;
				if( relFreq.containsKey( MISSING_VALUE ) )
					noOfMissingVals = relFreq.get( MISSING_VALUE );
				for( String key : relFreq.keySet() )
					relFreq.put( key, relFreq.get( key ) / (noOfRows - noOfMissingVals) ); 
				
	
				// 
				// Check whether an item needs to be melted 
				//
				
				// Create an ordered list of the keys (without missing values)
				List<Integer> intKeys = new LinkedList<Integer>();
				for( String key : relFreq.keySet() )
				{
					if( !key.equals( MISSING_VALUE ) )
					{
						intKeys.add( Integer.parseInt( key ) );
					}
				}
				Collections.sort( intKeys );
				List<String> orderedKeys = new LinkedList<String>();
				for( Integer key : intKeys )
				{
					orderedKeys.add( "" + key );
				}
					
				
				// Set shift direction to right
				boolean isDirRight = true;
				
				// Search for a key from the left up to the middle that is under the minimum
				String minKey = null;
				for( String key : orderedKeys.subList( 0, (orderedKeys.size() - 1) / 2) )
				{
					if( relFreq.get( key ) < min )
					{
						minKey = key;
						break;
					}
				}
				// If no such key is found
				if( minKey == null )
				{
					// Set shift direction to left
					isDirRight = false;
					
					// Search for a key from the right up to the middle that is under the minimum
					List<String> orderedKeysRightPart = new LinkedList<String>( orderedKeys.subList( (orderedKeys.size() / 2) + 1, orderedKeys.size() ) );
					Collections.reverse( orderedKeysRightPart );					
					for( String key : orderedKeysRightPart )
					{
						if( relFreq.get( key ) < min )
						{
							minKey = key;
							break;
						}
					}
				}
				
				
				// Do fusion
				isFusion = false;
				int j = orderedKeys.indexOf( minKey );
				if( minKey != null )
				{
					isFusion = true;

					// VERBOSE
					int noOfFusionedValues = 0;
					
					for( int k = 0; k < datatable.size(); k++ )
					{
						if( datatable.get( k ).get( i ).equals( orderedKeys.get( j ) ) )
						{
							// VERBOSE
							noOfFusionedValues++;
							
							String[] rowAsArray = datatable.get( k ).toArray( new String[ 0 ] );
							List<String> newRow = new LinkedList<String>();
							if( isDirRight )
							{
								// Create a new row with changed item value
								rowAsArray[ i ] = orderedKeys.get( j + 1 );
								for( String item : rowAsArray )
								{
									newRow.add( item );
								}									
							}
							else
							{
								// Create a new row with changed item value
								rowAsArray[ i ] = orderedKeys.get( j - 1 );
								for( String item : rowAsArray )
								{
									newRow.add( item );
								}
							}

							datatable.add( k, newRow );
							datatable.remove( k + 1 );
						}
					}

					// VERBOSE
					String orderedKeysWithRelFreq = "{";
					for( String key : orderedKeys )
					{
						orderedKeysWithRelFreq += key + " [" + Math.round( relFreq.get( key ) * 10000.0 ) / 10000.0 + "], ";
					}
					orderedKeysWithRelFreq = orderedKeysWithRelFreq.substring( 0, orderedKeysWithRelFreq.length() - 2 ) + "}";
					if( isDirRight )
					{							
						System.out.println( "Item " + i + " mit Ausprägungen " + orderedKeysWithRelFreq + ": Zusammenlegung " +  orderedKeys.get( j ) + " --> " + orderedKeys.get( j + 1 ) + " (" + noOfFusionedValues + " Werte)" );
					}
					else
					{
						System.out.println( "Item " + i + " mit Ausprägungen " + orderedKeysWithRelFreq + ": Zusammenlegung " +  orderedKeys.get( j - 1 ) + " <-- " + orderedKeys.get( j ) + " (" + noOfFusionedValues + " Werte)" );
					}
				}
			}

			
			//
			// VERBOSE
			//
			
			HashMap<String, Double> relFreq = new HashMap<String, Double>();
			
			// Count absolute frequencies
			for( List<String> row : datatable )
			{
				if( !relFreq.containsKey( row.get( i ) ) )
				{
					relFreq.put( row.get( i ), 0.0 );
				}
				relFreq.put( row.get( i ), relFreq.get( row.get( i ) ) + 1 );
			}
			
			// Normalize to relative frequencies
			double noOfMissingVals = 0;
			if( relFreq.containsKey( MISSING_VALUE ) )
				noOfMissingVals = relFreq.get( MISSING_VALUE );
			for( String key : relFreq.keySet() )
				relFreq.put( key, relFreq.get( key ) / (noOfRows - noOfMissingVals) ); 

			// Create an ordered list of the keys (without missing values)
			List<Integer> intKeys = new LinkedList<Integer>();
			for( String key : relFreq.keySet() )
			{
				if( !key.equals( MISSING_VALUE ) )
				{
					intKeys.add( Integer.parseInt( key ) );
				}
			}
			Collections.sort( intKeys );
			List<String> orderedKeys = new LinkedList<String>();
			for( Integer key : intKeys )
			{
				orderedKeys.add( "" + key );
			}

			String orderedKeysWithRelFreq = "{";
			for( String key : orderedKeys )
			{
				orderedKeysWithRelFreq += key + " [" + Math.round( relFreq.get( key ) * 10000.0 ) / 10000.0 + "], ";
			}
			orderedKeysWithRelFreq = orderedKeysWithRelFreq.substring( 0, orderedKeysWithRelFreq.length() - 2 ) + "}";
			
			System.out.println( "Item " + i + " mit Ausprägungen " + orderedKeysWithRelFreq );
			System.out.println();
		}	

		
		// VERBOSE
		System.out.println( "Neue Datei " + outfilename + " ausgeben..." );

		try 
		{
			BufferedWriter writer = new BufferedWriter( new FileWriter( outfilename ) );
			
			for( List<String> row : datatable )
			{
				for( int i = 0; i < row.size(); i++ )
				{
					writer.write( row.get( i ) );
					if( i < row.size() - 1 )
					{
						writer.write( "\t" );
					}
				}
				
				writer.write( "\r\n" );
			}
			
			writer.close();
		}
		catch( IOException e ) 
		{
			System.out.println( "FEHLER: Schreiben der Ausgabedatei " + outfilename + " nicht mögich!" );
			System.exit( 1 );
		}
		
		// VERBOSE
		System.out.println( "Verarbeitung abgeschlossen." );
	}
}
