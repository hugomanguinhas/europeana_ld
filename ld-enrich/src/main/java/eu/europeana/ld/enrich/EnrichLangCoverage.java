/**
 * 
 */
package eu.europeana.ld.enrich;


/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Nov 2016
 */
public class EnrichLangCoverage
{
    private int          _count;
    private LangCoverage _overallCoverage;
    private LangCoverage _inUseCoverage;
    private LangCoverage _enrichCoverage;

    public EnrichLangCoverage()
    {
        super();
        _count           = 0;
        _overallCoverage = new LangCoverage();
        _inUseCoverage   = new LangCoverage();
        _enrichCoverage  = new LangCoverage();
    }

    public void add2Overall(LangCoverage overall)
    {
        _count++;
        _overallCoverage.add(overall);
    }

    public void add2InUse(LangCoverage inUse)
    {
        _inUseCoverage.add(inUse);
    }

    public void add2Enrich(LangCoverage inUse)
    {
        _enrichCoverage.add(inUse);
    }

    public int          getTotal()   { return _count;           }
    public LangCoverage getOverall() { return _overallCoverage; }
    public LangCoverage getInUse()   { return _inUseCoverage;   }
    public LangCoverage getEnrich()  { return _enrichCoverage;  }
}