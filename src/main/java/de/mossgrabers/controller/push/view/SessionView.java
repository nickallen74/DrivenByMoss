// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<PushControlSurface, PushConfiguration>
{
    private static final int NUMBER_OF_RETRIES = 20;

    protected int            startRetries;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final PushControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 8, 8, true);

        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int redLo = isPush2 ? PushColors.PUSH2_COLOR2_RED_LO : PushColors.PUSH1_COLOR2_RED_LO;
        final int redHi = isPush2 ? PushColors.PUSH2_COLOR2_RED_HI : PushColors.PUSH1_COLOR2_RED_HI;
        final int black = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        final int green = isPush2 ? PushColors.PUSH2_COLOR2_GREEN : PushColors.PUSH1_COLOR2_GREEN;
        final int amber = isPush2 ? PushColors.PUSH2_COLOR2_AMBER : PushColors.PUSH1_COLOR2_AMBER;
        final SessionColor isRecording = new SessionColor (redHi, redHi, false);
        final SessionColor isRecordingQueued = new SessionColor (redHi, black, true);
        final SessionColor isPlaying = new SessionColor (green, green, false);
        final SessionColor isPlayingQueued = new SessionColor (green, black, true);
        final SessionColor hasContent = new SessionColor (amber, -1, false);
        final SessionColor noContent = new SessionColor (black, -1, false);
        final SessionColor recArmed = new SessionColor (redLo, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
        {
            final TriggerCommand triggerCommand = this.surface.getViewManager ().getView (Views.VIEW_SESSION).getTriggerCommand (Commands.COMMAND_SELECT_SESSION_VIEW);
            ((SelectSessionViewCommand) triggerCommand).setTemporary ();
            return;
        }

        final int index = note - 36;
        int t = index % this.columns;
        int s = this.rows - 1 - index / this.columns;
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        if (flipSession)
        {
            final int dummy = t;
            t = s;
            s = dummy;
        }
        final IChannelBank tb = this.model.getCurrentTrackBank ();

        // Birds-eye-view navigation
        if (this.surface.isShiftPressed ())
        {
            // Calculate page offsets
            final int numTracks = tb.getNumTracks ();
            final int numScenes = tb.getNumScenes ();
            final int trackPosition = tb.getTrack (0).getPosition () / numTracks;
            final int scenePosition = tb.getScenePosition () / numScenes;
            final int selX = flipSession ? scenePosition : trackPosition;
            final int selY = flipSession ? trackPosition : scenePosition;
            final int padsX = flipSession ? this.rows : this.columns;
            final int padsY = flipSession ? this.columns : this.rows;
            final int offsetX = selX / padsX * padsX;
            final int offsetY = selY / padsY * padsY;
            tb.scrollToChannel (offsetX * numTracks + t * padsX);
            tb.scrollToScene (offsetY * numScenes + s * padsY);
            return;
        }

        // Duplicate a clip
        final ITrack track = tb.getTrack (t);
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_DUPLICATE))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DUPLICATE);
            if (track.doesExist ())
                track.getSlot (s).duplicate ();
            return;
        }

        // Stop clip
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_CLIP_STOP);
            track.stop ();
            return;
        }

        // Browse for clips
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_BROWSE))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_BROWSE);
            if (!track.doesExist ())
                return;
            track.getSlot (s).browse ();
            final ModeManager modeManager = this.surface.getModeManager ();
            if (!modeManager.isActiveMode (Modes.MODE_BROWSER))
                this.activateMode ();
            return;
        }

        super.onGridNote (note, velocity);
    }


    /**
     * Tries to activate the mode 20 times.
     */
    protected void activateMode ()
    {
        if (this.model.getBrowser ().isActive ())
            this.surface.getModeManager ().setActiveMode (Modes.MODE_BROWSER);
        else if (this.startRetries < NUMBER_OF_RETRIES)
        {
            this.startRetries++;
            this.surface.scheduleTask (this::activateMode, 200);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int off = isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, sceneBank.sceneExists (7 - i) ? green : off);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }
}