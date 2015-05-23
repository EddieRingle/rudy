package io.ringle.rudy

import io.ringle.statesman.Stateful

public trait Routable : Attachable, Finishable, Navigable, Resumable {

    public var routeResolution: RouteResolution?
}
