package guang.crawler.centerConfig.sitesConfig;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * 所有采集点的信息类
 *
 * @author sun
 *
 */
public class SitesInfo extends CenterConfigElement {

	public SitesInfo(final String path, final ZookeeperConnector connector) {
		super(path, connector);
	}

	/**
	 * 删除某个站点
	 *
	 * @param siteId
	 *            采集点的ID
	 * @return 删除采集点,如果删除过程出错,则抛出异常,否则返回true.需要注意的是,如果该采集点本身不存在,那么也是返回true的.
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public boolean dropSite(final String siteId) throws KeeperException,
	        InterruptedException {
		String realPath = this.path + "/" + siteId;
		boolean exist = this.connector.isNodeExists(realPath);
		if (!exist) { // 如果本身该节点已经不存在了，那么再次删除也是正确的
			return true;
		}
		this.connector.simpleDelete(siteId, null);
		return true;

	}

	/**
	 * 获取所有已经处理的站点
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public List<SiteInfo> getAllHandledSites() throws InterruptedException,
	        KeeperException {
		LinkedList<SiteInfo> result = new LinkedList<SiteInfo>();
		List<String> children = this.connector.getChildren(this.path);
		for (String child : children) {
			try {
				SiteInfo siteInfo = this.getSite(child);
				if ((siteInfo != null) && siteInfo.isHandled()) {
					result.add(siteInfo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 获取所有站点
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public LinkedList<SiteInfo> getAllSites() throws InterruptedException,
	        KeeperException {
		LinkedList<SiteInfo> result = new LinkedList<SiteInfo>();
		List<String> children = this.connector.getChildren(this.path);
		for (String child : children) {
			try {
				SiteInfo siteInfo = this.getSite(child);
				result.add(siteInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 获取所有尚未处理的站点
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public LinkedList<SiteInfo> getAllUnhandledSites()
	        throws InterruptedException, KeeperException {
		LinkedList<SiteInfo> result = new LinkedList<SiteInfo>();
		List<String> children = this.connector.getChildren(this.path);
		for (String child : children) {
			try {
				SiteInfo siteInfo = this.getSite(child);
				if ((siteInfo != null) && !siteInfo.isHandled()
				        && siteInfo.isEnabled() && !siteInfo.isFinished()) {
					result.add(siteInfo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 根据站点ID获取采集点信息
	 *
	 * @param siteId
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo getSite(final String siteId) throws InterruptedException,
	        IOException, KeeperException {
		String realPath = this.path + "/" + siteId;
		boolean exist = this.connector.isNodeExists(realPath);
		if (!exist) {
			return null;
		}
		SiteInfo info = new SiteInfo(realPath, this.connector);
		info.load();
		return info;

	}

	/**
	 * 新增了一个站点
	 *
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public SiteInfo registSite(final String siteId)
	        throws InterruptedException, IOException, KeeperException {
		String realPath = this.connector.createNode(this.path + "/" + siteId,
		                                            CreateMode.PERSISTENT,
		                                            "".getBytes());
		if (realPath != null) {
			SiteInfo siteInfo = new SiteInfo(realPath, this.connector);
			return siteInfo;
		}
		return null;
	}
}
