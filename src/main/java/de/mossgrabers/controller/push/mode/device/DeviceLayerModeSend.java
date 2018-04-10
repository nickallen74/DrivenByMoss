// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.DisplayMessage;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;


/**
 * Mode for editing a all sends of a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerModeSend extends DeviceLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerModeSend (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannel layer = cd.getLayerOrDrumPad (offset + index);
        if (layer.doesExist ())
            cd.changeLayerOrDrumPadSend (offset + index, this.getCurrentSendIndex (), value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final ICursorDevice cd = this.model.getCursorDevice ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final IChannel layer = cd.getLayerOrDrumPad (offset + index);
        if (!layer.doesExist ())
            return;

        final int sendIndex = this.getCurrentSendIndex ();

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                cd.resetLayerOrDrumPadSend (offset + index, sendIndex);
                return;
            }

            final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
            final String name = fxTrackBank == null ? layer.getSend (sendIndex).getName () : fxTrackBank.getTrack (sendIndex).getName ();
            if (!name.isEmpty ())
                this.surface.getDisplay ().notify ("Send " + name + ": " + layer.getSend (sendIndex).getDisplayedValue ());
        }

        cd.touchLayerOrDrumPadSend (offset + index, sendIndex, isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ICursorDevice cd = this.model.getCursorDevice ();
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final int sendIndex = this.getCurrentSendIndex ();

        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (offset + i);
            final boolean exists = layer.doesExist ();
            final ISend send = layer.getSend (sendIndex);
            d.setCell (0, i, exists ? send.getName () : "").setCell (1, i, send.getDisplayedValue (8));
            if (exists)
                d.setCell (2, i, send.getValue (), Format.FORMAT_VALUE);
            else
                d.clearCell (2, i);
        }
        d.done (0).done (1).done (2);

        this.drawRow4 (d, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplayElements (final DisplayMessage message, final ICursorDevice cd, final IChannel l)
    {
        final int sendIndex = this.getCurrentSendIndex ();
        final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();

        this.updateMenu ();

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (offset + i);

            // The menu item
            String topMenu;
            boolean topMenuSelected;
            if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                topMenu = layer.doesExist () ? "Mute" : "";
                topMenuSelected = layer.isMute ();
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                topMenu = layer.doesExist () ? "Solo" : "";
                topMenuSelected = layer.isSolo ();
            }
            else
            {
                topMenu = this.menu[i];
                topMenuSelected = i > 3 && i - 4 + sendOffset == sendIndex;
            }

            // Channel info
            final String [] sendName = new String [4];
            final String [] valueStr = new String [4];
            final int [] value = new int [4];
            final int [] modulatedValue = new int [4];
            final boolean [] selected = new boolean [4];
            for (int j = 0; j < 4; j++)
            {
                final int sendPos = sendOffset + j;
                final ISend send = layer.getSend (sendPos);
                sendName[j] = fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ();
                valueStr[j] = send.doesExist () && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue () : "";
                value[j] = send.doesExist () ? send.getValue () : 0;
                modulatedValue[j] = send.doesExist () ? send.getModulatedValue () : 0;
                selected[j] = sendIndex == sendPos;
            }

            message.addSendsElement (topMenu, topMenuSelected, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, cd.getLayerOrDrumPad (offset + i).getColor (), layer.isSelected (), sendName, valueStr, value, modulatedValue, selected, false);
        }
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveModeId ().intValue () - Modes.MODE_DEVICE_LAYER_SEND1.intValue ();
    }
}