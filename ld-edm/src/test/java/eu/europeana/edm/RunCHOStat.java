package eu.europeana.edm;

import java.io.File;
import java.io.IOException;

import eu.europeana.ld.edm.analysis.CHOAnalysisOld;

public class RunCHOStat
{
   public static final void main(String... args) throws IOException
   {
      File f = new File("D:\\work\\incoming\\taskforce\\dataset\\dataset.xml");
      new CHOAnalysisOld().analyse(f);
   }
}
