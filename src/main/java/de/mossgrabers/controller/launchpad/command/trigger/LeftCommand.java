// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command to dis-/enable the metronome. Also toggles metronome ticks when Shift is pressed.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LeftCommand extends MetronomeCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public LeftCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        final View view = this.surface.getViewManager ().getActiveView ();
        if (index == -1 || this.surface.isShiftPressed ())
        {
            if (!tb.canScrollTracksUp ())
                return;
            tb.scrollTracksPageUp ();
            final int newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
            this.surface.scheduleTask ( () -> view.selectTrack (newSel), 75);
            return;
        }
        view.selectTrack (index);
    }
}
