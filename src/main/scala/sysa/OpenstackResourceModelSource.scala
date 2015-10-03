package sysa

import com.dtolabs.rundeck.core.common.INodeSet
import com.dtolabs.rundeck.core.resources.ResourceModelSource
import com.google.common.util.concurrent.Service.State

class OpenstackResourceModelSource(settings: OpenstackSettings) extends ResourceModelSource {

	private var nodeFetcher = new NodeFetcher(settings)

	nodeFetcher.startAsync()

	override def getNodes: INodeSet = {

		if (nodeFetcher.state == State.FAILED) {
			nodeFetcher = new NodeFetcher(settings)
			nodeFetcher.startAsync()
		}

		nodeFetcher.nodeSet.get()
	}
}
