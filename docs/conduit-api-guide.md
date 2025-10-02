# How to create a custom conduit?

The original author of this article is **Henry Loenwind** (from EnderIO dev team), 
we improved this article and split it from Javadocs.

Sorry, the conduit API is too involved to be put into the API jar.

Ok, it would be possible to do, but it would be much work and addons that implement conduits have no reason not to
compile against Ender IO proper, anyway.

So, to get started:
- Write an interface defining your conduit that extends `Conduit`.
- Write one or more classes implementing that.
- Add an `ConduitNetwork` if you want.
- Add an `ConduitRenderer`.
- Write your item class.
- Use the `ConduitBuilder` to define your conduit (Note: You can leave out the offsets to get assigned the first free offset).
- Register the definition with the `ConduitRegistry`.
- Register your renderer with the `ConduitBundleRenderManager` (that's in the conduits module at the moment...)

Oh, and if your conduit doesn't use Capabilities but needs an interface implemented on the `TileEntity`: Your problem.

Really, I'm tired of having a ConduitBundle TileEntity that has countless {@link Optional}-ed interfaces on it. Just
switch over to a Capability-based API---it isn't that hard.

Notes:

The Offset system will hopefully go away soon and be replaced with a dynamic conduit positioning. This will be a
breaking change for the rendering and
collision system, but it will become simpler, not harder. If you use the default renderer (like all built-in conduits
but the 2 lower-tier liquid conduits
do), you'll probably won't have to do much at all.
<p>
Please subscribe to the EnderIO github issue tracker and handle tickets about your conduits there. Because if your
conduits cause too many tickets there I may be tempted to blacklist them.

