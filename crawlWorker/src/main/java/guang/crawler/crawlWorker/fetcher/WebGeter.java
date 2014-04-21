package guang.crawler.crawlWorker.fetcher;

import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.parser.Parser;
import guang.crawler.crawlWorker.plugins.DownloadPlugin;

import java.util.LinkedList;

import org.apache.http.HttpStatus;

public class WebGeter
{
	private Parser	                   parser;
	private PageFetcher	               pageFetcher;
	private LinkedList<DownloadPlugin>	downloadPlugins;
	
	public WebGeter()
	{
		CrawlConfig config = new CrawlConfig();
		config.setIncludeHttpsPages(true);
		this.parser = new Parser(config);
		this.pageFetcher = new PageFetcher(config);
		this.downloadPlugins = new LinkedList<>();
	}
	
	public void addPlugin(DownloadPlugin plugin)
	{
		this.downloadPlugins.add(plugin);
	}
	
	private Page download(WebURL curURL)
	{
		PageFetchResult fetchResult = null;
		try
		{
			fetchResult = this.pageFetcher.fetchData(curURL);
			if (fetchResult.getStatusCode() == HttpStatus.SC_OK)
			{
				try
				{
					Page page = new Page(curURL);
					fetchResult.transformToPage(page);
					if (this.parser.parse(page, curURL.getURL()))
					{
						return page;
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} finally
		{
			if (fetchResult != null)
			{
				fetchResult.discardContentIfNotConsumed();
			}
		}
		return null;
	}
	
	public void processUrl(WebURL url)
	{
		System.out.println("Processing: " + url);
		Page page = this.download(url);
		if (page != null)
		{
			for (DownloadPlugin plugin : this.downloadPlugins)
			{
				boolean success = plugin.work(page);
				if (!success)
				{
					break;
				}
			}
		} else
		{
			System.out.println("Couldn't fetch the content of the page.");
		}
		System.out.println("==============");
	}
	
	public void shutdown()
	{
		if (this.pageFetcher != null)
		{
			this.pageFetcher.shutDown();
		}
	}
}
