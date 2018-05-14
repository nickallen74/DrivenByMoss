// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelImpl extends AbstractModel
{
    private ControllerHost controllerHost;
    private CursorTrack    cursorTrack;
    private BooleanValue   masterTrackEqualsValue;


    /**
     * Constructor.
     *
     * @param controllerHost The host
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param numTracks The number of track to monitor (per track bank)
     * @param numScenes The number of scenes to monitor (per scene bank)
     * @param numSends The number of sends to monitor
     * @param numFilterColumnEntries The number of entries in one filter column to monitor
     * @param numResults The number of search results in the browser to monitor
     * @param hasFlatTrackList Don't navigate groups, all tracks are flat
     * @param numParams The number of parameter of a device to monitor
     * @param numDevicesInBank The number of devices to monitor
     * @param numDeviceLayers The number of device layers to monitor
     * @param numDrumPadLayers The number of drum pad layers to monitor
     */
    public ModelImpl (final ControllerHost controllerHost, final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final int numTracks, final int numScenes, final int numSends, final int numFilterColumnEntries, final int numResults, final boolean hasFlatTrackList, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        super (colorManager, valueChanger, scales, numTracks, numScenes, numSends, numFilterColumnEntries, numResults, hasFlatTrackList, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        this.controllerHost = controllerHost;
        this.host = new HostImpl (controllerHost);

        final Application app = controllerHost.createApplication ();
        this.application = new ApplicationImpl (app);
        this.project = new ProjectImpl (controllerHost.getProject (), app);
        this.arranger = new ArrangerImpl (controllerHost.createArranger ());
        this.mixer = new MixerImpl (controllerHost.createMixer ());
        this.transport = new TransportImpl (controllerHost, valueChanger);
        this.groove = new GrooveImpl (controllerHost, valueChanger);
        final MasterTrack master = controllerHost.createMasterTrack (0);
        this.masterTrack = new MasterTrackImpl (master, valueChanger);

        this.cursorTrack = controllerHost.createCursorTrack ("MyCursorTrackID", "The Cursor Track", 0, 0, true);
        this.cursorTrack.isPinned ().markInterested ();

        this.trackBank = new TrackBankImpl (controllerHost, valueChanger, this.cursorTrack, this.numTracks, this.numScenes, this.numSends, this.hasFlatTrackList);
        this.effectTrackBank = new EffectTrackBankImpl (controllerHost, valueChanger, this.cursorTrack, this.numTracks, this.numScenes, this.trackBank);

        this.primaryDevice = new CursorDeviceImpl (this.host, this.cursorTrack.createCursorDevice ("FIRST_INSTRUMENT", "First Instrument", this.numSends, CursorDeviceFollowMode.FIRST_INSTRUMENT), valueChanger, this.numSends, this.numParams, this.numDevicesInBank, this.numDeviceLayers, this.numDrumPadLayers);
        this.primaryDevice.setDrumPadIndication (false);
        PinnableCursorDevice cd = this.cursorTrack.createCursorDevice ("CURSOR_DEVICE", "Cursor device", this.numSends, CursorDeviceFollowMode.FOLLOW_SELECTION);
        this.cursorDevice = new CursorDeviceImpl (this.host, cd, valueChanger, this.numSends, this.numParams, this.numDevicesInBank, this.numDeviceLayers, this.numDrumPadLayers);
        this.cursorDevice.setDrumPadIndication (false);
        if (this.numDrumPadLayers > 0)
        {
            cd = this.cursorTrack.createCursorDevice ("64_DRUM_PADS", "64 Drum Pads", 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
            this.drumDevice64 = new CursorDeviceImpl (this.host, cd, valueChanger, 0, 0, -1, 64, 64);
            this.drumDevice64.setDrumPadIndication (false);
        }
        if (this.numResults > 0)
            this.browser = new BrowserImpl (controllerHost.createPopupBrowser (), this.cursorTrack, this.cursorDevice, this.numFilterColumnEntries, this.numResults);

        this.masterTrackEqualsValue = cd.channel ().createEqualsValue (master);
        this.masterTrackEqualsValue.markInterested ();

        this.currentTrackBank = this.trackBank;

        // Make sure there is at least 1 cursor clip for quantization, even if there are no
        // sequencers
        this.getCursorClip ();
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank createSceneViewTrackBank (final int numTracks, final int numScenes)
    {
        return new TrackBankImpl (this.controllerHost, this.valueChanger, this.cursorTrack, numTracks, numScenes, 0, true);
    }


    /** {@inheritDoc} */
    @Override
    public ICursorClip getCursorClip (final int cols, final int rows)
    {
        return this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.controllerHost, this.valueChanger, cols, rows));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorTrackPinned ()
    {
        return this.cursorTrack.isPinned ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCursorTrackPinned ()
    {
        this.cursorTrack.isPinned ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorDeviceOnMasterTrack ()
    {
        return this.masterTrackEqualsValue.get ();
    }
}