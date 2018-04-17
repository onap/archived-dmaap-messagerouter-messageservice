/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *  
 *******************************************************************************/
package com.att.nsa.dmaap.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.json.JSONException;

import com.att.nsa.apiServer.CommonServlet;
import com.att.nsa.cambria.beans.DMaaPKafkaMetaBroker;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cmdtool.Command;
import com.att.nsa.cmdtool.CommandLineTool;
import com.att.nsa.cmdtool.CommandNotReadyException;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.configs.ConfigPath;
import com.att.nsa.configs.confimpl.EncryptingLayer;
import com.att.nsa.configs.confimpl.ZkConfigDb;
import com.att.nsa.drumlin.till.data.rrConvertor;
import com.att.nsa.drumlin.till.data.uniqueStringGenerator;
import com.att.nsa.drumlin.till.nv.impl.nvWriteableTable;
import com.att.nsa.security.db.BaseNsaApiDbImpl;
import com.att.nsa.security.db.EncryptingApiDbImpl;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKeyFactory;
import com.att.nsa.util.NsaClock;

public class ConfigTool extends CommandLineTool<ConfigToolContext>
{
	protected ConfigTool ()
	{
		super ( "Cambria API Config Tool", "cambriaConfig> " );

		super.registerCommand ( new ListTopicCommand  () );
		super.registerCommand ( new WriteTopicCommand  () );
		super.registerCommand ( new ReadTopicCommand  () );
		super.registerCommand ( new SetTopicOwnerCommand () );
		super.registerCommand ( new InitSecureTopicCommand () );
		super.registerCommand ( new ListApiKeysCommand () );
		super.registerCommand ( new PutApiCommand () );
		super.registerCommand ( new writeApiKeyCommand () );
		super.registerCommand ( new EncryptApiKeysCommand () );
		super.registerCommand ( new DecryptApiKeysCommand () );
		super.registerCommand ( new NodeFetchCommand () );
		super.registerCommand ( new DropOldConsumerGroupsCommand () );
	}

	public static void main ( String[] args ) throws IOException
	{
		final String connStr = args.length>0 ? args[0] : "localhost:2181"; 
		final ConfigDb db = new ZkConfigDb (
			connStr,
			args.length>1 ? args[1] : CommonServlet.getDefaultZkRoot ( "cambria" )
		);

		final ConfigToolContext context = new ConfigToolContext ( db, connStr, new nvWriteableTable() );
		final ConfigTool ct = new ConfigTool ();
		ct.runFromMain ( args, context );
	}

	private static class ListTopicCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "topics", "list (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final ConfigDb db = context.getDb();
				final ConfigPath base = db.parse ( "/topics" );
				
				if ( parts.length > 0 )
				{
					final ConfigPath myTopic = base.getChild ( parts[0] );
					final String data = db.load ( myTopic );
					if ( data != null )
					{
						out.println ( data );
					}
					else
					{
						out.println ( "No topic [" + parts[0] + "]" );
					}
				}
				else
				{
					for ( ConfigPath child : db.loadChildrenNames ( base ) )
					{
						out.println ( child.getName () );
					}
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e);
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "topics" );
			out.println ( "list <topic>" );
		}
	}

	private static class WriteTopicCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "write (\\S*) (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final ConfigDb db = context.getDb();
				final ConfigPath base = db.parse ( "/topics" );
				final ConfigPath myTopic = base.getChild ( parts[0] );
				db.store ( myTopic, parts[1] );
				out.println ( "wrote [" + parts[1] + "] to topic [" + parts[0] + "]" );
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
				throw new RuntimeException(e);
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "write <topic> <string>" );
			out.println ( "\tBe careful with this. You can write data that's not compatible with Cambria's config db." );
		}
	}

	private static class ReadTopicCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "read (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final ConfigDb db = context.getDb();
				final ConfigPath base = db.parse ( "/topics" );
				final ConfigPath myTopic = base.getChild ( parts[0] );
				db.store ( myTopic, parts[1] );
				out.println ( "wrote [" + parts[1] + "] to topic [" + parts[0] + "]" );
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e);
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "read <topic>" );
			out.println ( "\tRead config data for a topic." );
		}
	}

	private static class InitSecureTopicCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "initTopic (\\S*) (\\S*) (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				DMaaPKafkaMetaBroker.createTopicEntry ( context.getDb (),
					context.getDb ().parse("/topics"), parts[0], parts[2], parts[1],true );
				out.println ( "Topic [" + parts[0] + "] updated." );
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e);
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "initTopic <topic> <ownerApiKey> <description>" );
		}
	}

	private static class SetTopicOwnerCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "setOwner (\\S*) (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final Topic kt = DMaaPKafkaMetaBroker.getKafkaTopicConfig ( context.getDb(),
					context.getDb().parse ( "/topics" ), parts[0] );
				if ( kt != null )
				{
					final String desc = kt.getDescription ();

					DMaaPKafkaMetaBroker.createTopicEntry ( context.getDb (),
						context.getDb ().parse("/topics"), parts[0], desc, parts[1], true );
					out.println ( "Topic [" + parts[0] + "] updated." );
				}
				else
				{
					out.println ( "Topic [" + parts[0] + "] doesn't exist." );
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage () );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "setOwner <topic> <ownerApiKey>" );
		}
	}

	private static class ListApiKeysCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "listApiKeys", "listApiKey (\\S*) (\\S*) (\\S*)", "listApiKey (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final ConfigDb db = context.getDb ();
				if ( parts.length == 0 )
				{
					final BaseNsaApiDbImpl<NsaSimpleApiKey> readFrom = new BaseNsaApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory () );
					int count = 0;
					for ( String key : readFrom.loadAllKeys () )
					{
						out.println ( key );
						count++;
					}
					out.println ( "" + count + " records." );
				}
				else
				{
					BaseNsaApiDbImpl<NsaSimpleApiKey> readFrom = new BaseNsaApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory () );
					if ( parts.length == 3 )
					{
						readFrom = new EncryptingApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory (),
							EncryptingLayer.readSecretKey ( parts[1] ), rrConvertor.base64Decode ( parts[2] ) );
					}
					final NsaSimpleApiKey apikey = readFrom.loadApiKey ( parts[0] );
					if ( apikey == null )
					{
						out.println ( "Key '" + parts[0] + "' not found." );
					}
					else
					{
						out.println ( apikey.asJsonObject ().toString () );
					}
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( JSONException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "listApiKeys" );
			out.println ( "listApiKey <key>" );
			out.println ( "listApiKey <key> <dbKey> <dbIv>" );
		}
	}

	private static class PutApiCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[]
			{
				// these are <key> <enckey> <encinit> <value>
				"putApiKey (secret) (\\S*) (\\S*) (\\S*) (\\S*)",
				"putApiKey (email) (\\S*) (\\S*) (\\S*) (\\S*)",
				"putApiKey (description) (\\S*) (\\S*) (\\S*) (\\S*)"
			};
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final ConfigDb db = context.getDb ();
				if ( parts.length == 5 )
				{
					final BaseNsaApiDbImpl<NsaSimpleApiKey> apiKeyDb =
						new EncryptingApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory (),
							EncryptingLayer.readSecretKey ( parts[2] ), rrConvertor.base64Decode ( parts[3] ) );

					final NsaSimpleApiKey apikey = apiKeyDb.loadApiKey ( parts[1] );
					if ( apikey == null )
					{
						out.println ( "Key '" + parts[1] + "' not found." );
					}
					else
					{
						if ( parts[0].equalsIgnoreCase ( "secret" ) )
						{
							apikey.resetSecret ( parts[4] );
						}
						else if ( parts[0].equalsIgnoreCase ( "email" ) )
						{
							apikey.setContactEmail ( parts[4] );
						}
						else if ( parts[0].equalsIgnoreCase ( "description" ) )
						{
							apikey.setDescription ( parts[4] );
						}

						apiKeyDb.saveApiKey ( apikey );
						out.println ( apikey.asJsonObject ().toString () );
					}
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( JSONException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "putApiKey secret <apiKey> <dbKey> <dbIv> <newSecret>" );
			out.println ( "putApiKey email <apiKey> <dbKey> <dbIv> <newEmail>" );
			out.println ( "putApiKey description <apiKey> <dbKey> <dbIv> <newDescription>" );
		}
	}

	private static class writeApiKeyCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[]
			{
				// <enckey> <encinit> <key> <secret>
				"writeApiKey (\\S*) (\\S*) (\\S*) (\\S*)",
			};
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final ConfigDb db = context.getDb ();
				if ( parts.length == 4 )
				{
					final BaseNsaApiDbImpl<NsaSimpleApiKey> apiKeyDb =
						new EncryptingApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory (),
							EncryptingLayer.readSecretKey ( parts[0] ), rrConvertor.base64Decode ( parts[1] ) );

					apiKeyDb.deleteApiKey ( parts[2] );
					final NsaSimpleApiKey apikey = apiKeyDb.createApiKey ( parts[2], parts[3] );
					out.println ( apikey.asJsonObject ().toString () );
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( JSONException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( KeyExistsException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "writeApiKey <dbKey> <dbIv> <newApiKey> <newSecret>" );
		}
	}

	private static class EncryptApiKeysCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "convertApiKeyDb", "convertApiKeyDb (\\S*) (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final String key = parts.length == 2 ? parts[0] : EncryptingLayer.createSecretKey ();
				final String iv = parts.length == 2 ? parts[1] : rrConvertor.base64Encode ( uniqueStringGenerator.createValue ( 16 ) );

				// This doesn't do well when the number of API keys is giant...
				if ( parts.length == 0 )
				{
					out.println ( "YOU MUST RECORD THESE VALUES AND USE THEM IN THE SERVER CONFIG" );
					out.println ( "Key: " + key );
					out.println ( " IV: " + iv );
					out.println ( "\n" );
					out.println ( "Call again with key and IV on command line." );
					out.println ( "\n" );
					return;	// because otherwise the values get lost 
				}

				final ConfigDb db = context.getDb ();
				final BaseNsaApiDbImpl<NsaSimpleApiKey> readFrom = new BaseNsaApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory () );
				final EncryptingApiDbImpl<NsaSimpleApiKey> writeTo = new EncryptingApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory (),
					EncryptingLayer.readSecretKey ( key ), rrConvertor.base64Decode ( iv ) );

				int count = 0;
				for ( Entry<String, NsaSimpleApiKey> e : readFrom.loadAllKeyRecords ().entrySet () )
				{
					out.println ( "-------------------------------" );
					out.println ( "Converting " + e.getKey () );
					final String was = e.getValue ().asJsonObject ().toString ();
					out.println ( was );

					writeTo.saveApiKey ( e.getValue () );
					count++;
				}

				out.println ( "Conversion complete, converted " + count + " records." );
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( NoSuchAlgorithmException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "convertApiKeyDb" );
			out.println ( "\tconvert an API key DB to an encrypted DB and output the cipher details" );
		}
	}

	private static class DecryptApiKeysCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "revertApiKeyDb (\\S*) (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final String keyStr = parts[0];
				final String iv = parts[1];
				final byte[] ivBytes = rrConvertor.base64Decode ( iv );

				final ConfigDb db = context.getDb ();
				final EncryptingApiDbImpl<NsaSimpleApiKey> readFrom = new EncryptingApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory (),
					EncryptingLayer.readSecretKey ( keyStr ), ivBytes );
				final BaseNsaApiDbImpl<NsaSimpleApiKey> writeTo = new BaseNsaApiDbImpl<NsaSimpleApiKey> ( db, new NsaSimpleApiKeyFactory () );

				int count = 0;
				for ( String apiKey : readFrom.loadAllKeys () )
				{
					out.println ( "Converting " + apiKey );
					final NsaSimpleApiKey record = readFrom.loadApiKey ( apiKey );
					if ( record == null )
					{
						out.println ( "Couldn't load " + apiKey );
					}
					else
					{
						writeTo.saveApiKey ( record );
						count++;
					}
				}
				out.println ( "Conversion complete, converted " + count + " records." );
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "revertApiKeyDb <keyBase64> <ivBase64>" );
			out.println ( "\trevert an API key DB to a deencrypted DB" );
		}
	}

	private static class NodeFetchCommand implements Command<ConfigToolContext>
	{
		@Override
		public String[] getMatches ()
		{
			return new String[] { "node (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final String node = parts[0];

				final ConfigDb db = context.getDb ();
				final ConfigPath cp = db.parse ( node );

				boolean doneOne = false;
				for ( ConfigPath child : db.loadChildrenNames ( cp ) )
				{
					out.println ( "\t- " + child.getName () );
					doneOne = true;
				}
				if ( doneOne )
				{
					out.println ();
				}
				else
				{
					out.println ( "(No child nodes of '" + node + "')" );
				}

				final String val = db.load ( cp );
				if ( val == null )
				{
					out.println ( "(No data at '" + node + "')" );
				}
				else
				{
					out.println ( val );
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( IllegalArgumentException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "node <nodeName>" );
			out.println ( "\tread a config db node" );
		}
	}

	private static class DropOldConsumerGroupsCommand implements Command<ConfigToolContext>
	{
		private final long kMaxRemovals = 500;
		
		@Override
		public String[] getMatches ()
		{
			return new String[] { "(dropOldConsumers) (\\S*)", "(showOldConsumers) (\\S*)" };
		}

		@Override
		public void checkReady ( ConfigToolContext context ) throws CommandNotReadyException
		{
		}

		@Override
		public void execute ( String[] parts, ConfigToolContext context, PrintStream out ) throws CommandNotReadyException
		{
			try
			{
				final boolean runDrops = parts[0].equalsIgnoreCase ( "dropOldConsumers" );
				final String maxAgeInDaysStr = parts[1];
				final int maxAgeInDays = Integer.parseInt ( maxAgeInDaysStr );
				final long oldestEpochSecs = ( NsaClock.now () / 1000 ) - ( 24 * 60 * 60 * maxAgeInDays );

				out.println ( "Dropping consumer groups older than " + new Date ( oldestEpochSecs * 1000 ) );

				final ConfigDb db = context.getDb ();

				// kafka updates consumer partition records in ZK each time a message
				// is served. we can determine which consumers are old based on a lack
				// of update to the partition entries
				// (see https://cwiki.apache.org/confluence/display/KAFKA/Kafka+data+structures+in+Zookeeper)

				// kafka only works with ZK, and our configDb was constructed with a non-kafka
				// root node. We have to switch it to get to the right content...
				if ( ! ( db instanceof ZkConfigDb ) )
				{
					throw new ConfigDbException ( "You can only show/drop old consumers against a ZK config db." );
				}

				final ZkConfigDb newZkDb = new ZkConfigDb ( context.getConnectionString (), "" );
				long cgCount = 0;

				final LinkedList<ConfigPath> removals = new LinkedList<ConfigPath> ();
				for ( ConfigPath consumerGroupName : newZkDb.loadChildrenNames ( newZkDb.parse ( "/consumers" ) ) )
				{
					cgCount++;
					if ( cgCount % 500 == 0 )
					{
						out.println ( "" + cgCount + " groups examined" );
					}

					boolean foundAnything = false;
					boolean foundRecentUse = false;
					long mostRecent = -1;

					// each consumer group has an "offsets" entry, which contains 0 or more topic entries.
					// each topic contains partition nodes.
					for ( ConfigPath topic : newZkDb.loadChildrenNames ( consumerGroupName.getChild ( "offsets" ) ) )
					{
						for ( ConfigPath offset : newZkDb.loadChildrenNames ( topic ) )
						{
							foundAnything = true;

							final long modTime = newZkDb.getLastModificationTime ( offset );
							mostRecent = Math.max ( mostRecent, modTime );

							foundRecentUse = ( modTime > oldestEpochSecs );
							if ( foundRecentUse ) break;
						}
						if ( foundRecentUse ) break;
					}

					// decide if this consumer group is old
					out.println ( "Group " + consumerGroupName.getName () + " was most recently used " + new Date ( mostRecent*1000 ) );
					if ( foundAnything && !foundRecentUse )
					{
						removals.add ( consumerGroupName );
					}

					if ( removals.size () >= kMaxRemovals )
					{
						break;
					}
				}

				// removals
				for ( ConfigPath consumerGroupName : removals )
				{
					out.println ( "Group " + consumerGroupName.getName () + " has no recent activity." );
					if ( runDrops )
					{
						out.println ( "Removing group " + consumerGroupName.getName () + "..." );
						newZkDb.clear ( consumerGroupName );
					}
				}
			}
			catch ( ConfigDbException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( NumberFormatException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
			catch ( JSONException e )
			{
				out.println ( "Command failed: " + e.getMessage() );
			}
		}

		@Override
		public void displayHelp ( PrintStream out )
		{
			out.println ( "showOldConsumers <minAgeInDays>" );
			out.println ( "dropOldConsumers <minAgeInDays>" );
			out.println ( "\tDrop (or just show) any consumer group that has been inactive longer than <minAgeInDays> days." );
			out.println ();
			out.println ( "\tTo be safe, <minAgeInDays> should be much higher than the maximum storage time on the Kafka topics." );
			out.println ( "\tA very old consumer will potentially miss messages, but will resume at the oldest message, while a" );
			out.println ( "\tdeleted consumer will start at the current message if it ever comes back." );
			out.println ();
			out.println ( "\tNote that show/drops are limited to " + kMaxRemovals + " records per invocation." );
		}
	}
}
