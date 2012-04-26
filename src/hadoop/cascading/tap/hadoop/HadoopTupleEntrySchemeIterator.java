/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.tap.hadoop;

import java.io.IOException;

import cascading.flow.FlowProcess;
import cascading.flow.SliceCounters;
import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tap.hadoop.util.TimedRecordReader;
import cascading.tuple.TupleEntrySchemeIterator;
import cascading.util.CloseableIterator;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;

/**
 *
 */
public class HadoopTupleEntrySchemeIterator<SourceContext> extends TupleEntrySchemeIterator<FlowProcess<JobConf>, JobConf, SourceContext, RecordReader>
  {
  private TimedRecordReader timedRecordReader;

  public HadoopTupleEntrySchemeIterator( FlowProcess<JobConf> flowProcess, Tap parentTap, RecordReader recordReader )
    {
    this( flowProcess, parentTap.getScheme(), new RecordReaderIterator( recordReader ) );
    }

  public HadoopTupleEntrySchemeIterator( FlowProcess<JobConf> flowProcess, Tap parentTap ) throws IOException
    {
    this( flowProcess, parentTap.getScheme(), new MultiRecordReaderIterator( flowProcess, parentTap ) );
    }

  public HadoopTupleEntrySchemeIterator( FlowProcess<JobConf> flowProcess, Scheme scheme, CloseableIterator<RecordReader> closeableIterator )
    {
    super( flowProcess, scheme, closeableIterator, flowProcess.getStringProperty( "cascading.source.path" ) );
    }

  @Override
  protected RecordReader wrapInput( RecordReader recordReader )
    {
    if( timedRecordReader == null )
      timedRecordReader = new TimedRecordReader( getFlowProcess(), SliceCounters.Read_Duration );

    timedRecordReader.setRecordReader( recordReader );

    return timedRecordReader;
    }
  }
