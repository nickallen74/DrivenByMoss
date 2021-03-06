// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.DisplayMessage;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Mode for editing a device layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerMode extends BaseMode
{
    protected final List<Pair<String, Boolean>> menu = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceLayerMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;

        for (int i = 0; i < 8; i++)
            this.menu.add (new Pair<> (" ", Boolean.FALSE));
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannel selectedDeviceLayer = cd.getSelectedLayerOrDrumPad ();
        if (selectedDeviceLayer == null)
            return;
        switch (index)
        {
            case 0:
                cd.changeLayerOrDrumPadVolume (selectedDeviceLayer.getIndex (), value);
                break;
            case 1:
                cd.changeLayerOrDrumPadPan (selectedDeviceLayer.getIndex (), value);
                break;
            default:
                if (this.isPush2 && index < 4)
                    break;
                final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                cd.changeLayerOrDrumPadSend (selectedDeviceLayer.getIndex (), sendIndex, value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannel l = cd.getSelectedLayerOrDrumPad ();
        if (l == null)
            return;

        this.isKnobTouched[index] = isTouched;

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                switch (index)
                {
                    case 0:
                        cd.resetLayerOrDrumPadVolume (l.getIndex ());
                        break;
                    case 1:
                        cd.resetLayerOrDrumPadPan (l.getIndex ());
                        break;
                    default:
                        if (this.isPush2 && index < 4)
                            break;
                        final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                        cd.resetLayerSend (l.getIndex (), sendIndex);
                        break;
                }
                return;
            }

            switch (index)
            {
                case 0:
                    this.surface.getDisplay ().notify ("Volume: " + l.getVolumeStr ());
                    break;
                case 1:
                    this.surface.getDisplay ().notify ("Pan: " + l.getPanStr ());
                    break;
                default:
                    if (this.isPush2 && index < 4)
                        break;
                    final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                    final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                    final String name = fxTrackBank == null ? l.getSend (sendIndex).getName () : fxTrackBank.getTrack (sendIndex).getName ();
                    if (!name.isEmpty ())
                        this.surface.getDisplay ().notify ("Send " + name + ": " + l.getSend (sendIndex).getDisplayedValue ());
                    break;
            }
        }

        switch (index)
        {
            case 0:
                cd.touchLayerOrDrumPadVolume (l.getIndex (), isTouched);
                break;
            case 1:
                cd.touchLayerOrDrumPadPan (l.getIndex (), isTouched);
                break;
            default:
                if (this.isPush2 && index < 4)
                    break;
                final int sendIndex = index - (this.isPush2 ? this.surface.getConfiguration ().isSendsAreToggled () ? 0 : 4 : 2);
                cd.touchLayerOrDrumPadSend (l.getIndex (), sendIndex, isTouched);
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        if (event == ButtonEvent.UP)
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            if (!cd.hasSelectedDevice ())
                return;

            final int offset = getDrumPadIndex (cd);
            final IChannel layer = cd.getLayerOrDrumPad (offset + index);
            if (!layer.doesExist ())
                return;

            final int layerIndex = layer.getIndex ();
            if (!layer.isSelected ())
            {
                cd.selectLayerOrDrumPad (layerIndex);
                return;
            }

            cd.enterLayerOrDrumPad (layer.getIndex ());
            cd.selectFirstDeviceInLayerOrDrumPad (layer.getIndex ());
            final ModeManager modeManager = this.surface.getModeManager ();
            modeManager.setActiveMode (Modes.MODE_DEVICE_PARAMS);
            ((DeviceParamsMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).setShowDevices (true);
            return;
        }

        // LONG press
        this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_ROW1_1 + index);
        this.moveUp ();
    }


    /**
     * Move up the hierarchy.
     */
    protected void moveUp ()
    {
        // There is no device on the track move upwards to the track view
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_TRACK, ButtonEvent.DOWN);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (Modes.MODE_DEVICE_PARAMS);
        cd.selectChannel ();
        ((DeviceParamsMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).setShowDevices (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final PushConfiguration config = this.surface.getConfiguration ();
        if (!this.isPush2 || config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            final int offset = getDrumPadIndex (cd);
            if (config.isMuteState ())
                cd.toggleLayerOrDrumPadMute (offset + index);
            else
                cd.toggleLayerOrDrumPadSolo (offset + index);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        IChannelBank fxTrackBank;
        switch (index)
        {
            case 0:
                if (modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_VOLUME))
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
                else
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER_VOLUME);
                break;

            case 1:
                if (modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_PAN))
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
                else
                    modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER_PAN);
                break;

            case 2:
                // Not used
                break;

            case 3:
                if (this.model.isEffectTrackBankActive ())
                    return;
                // Check if there are more than 4 FX channels
                if (!config.isSendsAreToggled ())
                {
                    fxTrackBank = this.model.getEffectTrackBank ();
                    if (!fxTrackBank.getTrack (4).doesExist ())
                        return;
                }
                config.setSendsAreToggled (!config.isSendsAreToggled ());

                if (!modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER))
                    modeManager.setActiveMode (Integer.valueOf (Modes.MODE_DEVICE_LAYER_SEND1.intValue () + (config.isSendsAreToggled () ? 4 : 0)));
                break;

            case 7:
                if (this.surface.isShiftPressed ())
                    this.handleSendEffect (config.isSendsAreToggled () ? 7 : 3);
                else
                    this.moveUp ();
                break;

            default:
                this.handleSendEffect (index - (config.isSendsAreToggled () ? 0 : 4));
                break;
        }
    }


    /**
     * Handle the selection of a send effect.
     *
     * @param sendIndex The index of the send
     */
    protected void handleSendEffect (final int sendIndex)
    {
        if (this.model.isEffectTrackBankActive ())
            return;
        final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
        if (!fxTrackBank.getTrack (sendIndex).doesExist ())
            return;
        final Integer si = Integer.valueOf (Modes.MODE_DEVICE_LAYER_SEND1.intValue () + sendIndex);
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (modeManager.isActiveMode (si) ? Modes.MODE_DEVICE_LAYER : si);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            d.setBlock (1, 0, "           Select").setBlock (1, 1, "a device or press").setBlock (1, 2, "'Add Effect'...  ").allDone ();
            return;
        }

        final boolean noLayers = cd.hasLayers () && cd.hasZeroLayers ();
        if (noLayers)
        {
            d.setBlock (1, 1, "    Please create").setBlock (1, 2, cd.hasDrumPads () ? "a Drum Pad..." : "a Device Layer...");
        }
        else
        {
            final IChannel l = cd.getSelectedLayerOrDrumPad ();
            if (l != null)
            {
                d.setCell (0, 0, "Volume").setCell (1, 0, l.getVolumeStr (8)).setCell (2, 0, this.surface.getConfiguration ().isEnableVUMeters () ? l.getVu () : l.getVolume (), Format.FORMAT_VALUE);
                d.setCell (0, 1, "Pan").setCell (1, 1, l.getPanStr (8)).setCell (2, 1, l.getPan (), Format.FORMAT_PAN);

                final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                if (fxTrackBank == null)
                {
                    for (int i = 0; i < 6; i++)
                    {
                        final int pos = 2 + i;
                        final ISend send = l.getSend (i);
                        d.setCell (0, pos, send.getName ()).setCell (1, pos, send.getDisplayedValue (8)).setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                    }
                }
                else
                {
                    final boolean isFX = this.model.isEffectTrackBankActive ();
                    for (int i = 0; i < 6; i++)
                    {
                        final ITrack fxTrack = fxTrackBank.getTrack (i);
                        final boolean isEmpty = isFX || !fxTrack.doesExist ();
                        final int pos = 2 + i;
                        if (isEmpty)
                        {
                            d.clearCell (0, pos);
                            d.clearCell (2, pos);
                        }
                        else
                        {
                            final ISend send = l.getSend (i);
                            d.setCell (0, pos, fxTrack.getName ()).setCell (1, pos, send.getDisplayedValue (8));
                            d.setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                        }
                    }
                }
            }
        }

        this.drawRow4 (d, cd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = display.createMessage ();
        if (!cd.hasSelectedDevice ())
        {
            for (int i = 0; i < 8; i++)
                message.addOptionElement (i == 2 ? "Please select a device or press 'Add Device'..." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            display.send (message);
            return;
        }

        final boolean noLayers = cd.hasLayers () && cd.hasZeroLayers ();
        if (noLayers)
        {
            for (int i = 0; i < 8; i++)
                message.addOptionElement (i == 3 ? "Please create a " + (cd.hasDrumPads () ? "Drum Pad..." : "Device Layer...") : "", i == 7 ? "Up" : "", true, "", "", false, true);
            display.send (message);
            return;
        }

        this.updateDisplayElements (message, cd, cd.getSelectedLayerOrDrumPad ());
        display.send (message);
    }


    /**
     * Update all 8 elements.
     *
     * @param message The display message
     * @param cd The cursor device
     * @param l The channel data
     */
    protected void updateDisplayElements (final DisplayMessage message, final ICursorDevice cd, final IChannel l)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        // Get the index at which to draw the Sends element
        int sendsIndex = l == null ? -1 : l.getIndex () - offset + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateMenuItems (5 + sendsIndex % 4);

        final PushConfiguration config = this.surface.getConfiguration ();

        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (offset + i);

            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();

            // Channel info
            final String bottomMenu = layer.doesExist () ? layer.getName (12) : "";
            final double [] bottomMenuColor = layer.getColor ();
            final boolean isBottomMenuOn = layer.isSelected ();

            if (layer.isSelected ())
            {
                final IValueChanger valueChanger = this.model.getValueChanger ();
                message.addChannelElement (topMenu, isTopMenuOn, bottomMenu, ChannelType.LAYER, bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), this.isKnobTouched[0] ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), this.isKnobTouched[1] ? layer.getPanStr (8) : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? layer.getVu () : 0), layer.isMute (), layer.isSolo (), false, 0);
            }
            else if (sendsIndex == i && l != null)
            {
                final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                final String [] sendName = new String [4];
                final String [] valueStr = new String [4];
                final int [] value = new int [4];
                final int [] modulatedValue = new int [4];
                final boolean [] selected = new boolean [4];
                for (int j = 0; j < 4; j++)
                {
                    final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
                    final int sendPos = sendOffset + j;
                    final ISend send = l.getSend (sendPos);
                    sendName[j] = fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ();
                    final boolean doesExist = send.doesExist ();
                    valueStr[j] = doesExist && this.isKnobTouched[4 + j] ? send.getDisplayedValue () : "";
                    value[j] = doesExist ? send.getValue () : 0;
                    modulatedValue[j] = doesExist ? send.getModulatedValue () : 0;
                    selected[j] = true;
                }
                message.addSendsElement (topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, cd.getLayerOrDrumPad (offset + i).getColor (), layer.isSelected (), sendName, valueStr, value, modulatedValue, selected, true);
            }
            else
                message.addChannelSelectorElement (topMenu, isTopMenuOn, bottomMenu, ChannelType.LAYER, bottomMenuColor, isBottomMenuOn);
        }
    }


    // Called from sub-classes
    protected void updateChannelDisplay (final DisplayMessage message, final ICursorDevice cd, final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateMenuItems (selectedMenu);

        final PushConfiguration config = this.surface.getConfiguration ();

        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);

        final IValueChanger valueChanger = this.model.getValueChanger ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (offset + i);
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();
            message.addChannelElement (selectedMenu, topMenu, isTopMenuOn, layer.doesExist () ? layer.getName () : "", ChannelType.LAYER, cd.getLayerOrDrumPad (offset + i).getColor (), layer.isSelected (), valueChanger.toDisplayValue (layer.getVolume ()), valueChanger.toDisplayValue (layer.getModulatedVolume ()), isVolume && this.isKnobTouched[i] ? layer.getVolumeStr (8) : "", valueChanger.toDisplayValue (layer.getPan ()), valueChanger.toDisplayValue (layer.getModulatedPan ()), isPan && this.isKnobTouched[i] ? layer.getPanStr () : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? layer.getVu () : 0), layer.isMute (), layer.isSolo (), false, 0);
        }
    }


    protected void updateMenuItems (final int selectedMenu)
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            this.updateMuteMenu ();
        else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            this.updateSoloMenu ();
        else
            this.updateLayerMenu (selectedMenu);
    }


    protected void updateSoloMenu ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (i);
            this.menu.get (i).set (layer.doesExist () ? "Solo" : "", Boolean.valueOf (layer.isSolo ()));
        }
    }


    protected void updateMuteMenu ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (i);
            this.menu.get (i).set (layer.doesExist () ? "Mute" : "", Boolean.valueOf (layer.isMute ()));
        }
    }


    protected void updateLayerMenu (final int selectedMenu)
    {
        final PushConfiguration config = this.surface.getConfiguration ();

        this.menu.get (0).set ("Volume", Boolean.valueOf (selectedMenu - 1 == 0));
        this.menu.get (1).set ("Pan", Boolean.valueOf (selectedMenu - 1 == 1));
        this.menu.get (2).set (" ", Boolean.FALSE);

        if (this.model.isEffectTrackBankActive ())
        {
            // No sends for FX tracks
            for (int i = 3; i < 7; i++)
                this.menu.get (i).set (" ", Boolean.FALSE);
            return;
        }

        final boolean sendsAreToggled = config.isSendsAreToggled ();

        this.menu.get (3).set (sendsAreToggled ? "Sends 5-8" : "Sends 1-4", Boolean.valueOf (sendsAreToggled));

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final int sendOffset = sendsAreToggled ? 4 : 0;
        final boolean isShiftPressed = this.surface.isShiftPressed ();
        for (int i = 0; i < (isShiftPressed ? 4 : 3); i++)
        {
            final String sendName = tb.getEditSendName (sendOffset + i);
            this.menu.get (4 + i).set (sendName.isEmpty () ? " " : sendName, Boolean.valueOf (4 + i == selectedMenu - 1));
        }

        if (!isShiftPressed)
            this.menu.get (7).set ("Up", Boolean.TRUE);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd == null || !cd.hasLayers ())
        {
            this.disableFirstRow ();
            return;
        }

        final int offset = getDrumPadIndex (cd);
        for (int i = 0; i < 8; i++)
        {
            final IChannel dl = cd.getLayerOrDrumPad (offset + i);
            this.surface.updateButton (20 + i, dl.doesExist () && dl.isActivated () ? dl.isSelected () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO : this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();

        final PushConfiguration config = this.surface.getConfiguration ();
        final boolean muteState = config.isMuteState ();
        if (this.isPush2)
        {
            if (config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
            {
                // Drum Pad Bank has size of 16, layers only 8
                final int offset = getDrumPadIndex (cd);

                for (int i = 0; i < 8; i++)
                {
                    final IChannel layer = cd.getLayerOrDrumPad (offset + i);

                    int color = PushColors.PUSH2_COLOR_BLACK;
                    if (layer.doesExist ())
                    {
                        if (muteState)
                        {
                            if (layer.isMute ())
                                color = PushColors.PUSH2_COLOR2_AMBER_LO;
                        }
                        else if (layer.isSolo ())
                            color = PushColors.PUSH2_COLOR2_YELLOW_HI;
                    }

                    this.surface.updateButton (102 + i, color);
                }
                return;
            }

            final ModeManager modeManager = this.surface.getModeManager ();
            this.surface.updateButton (102, modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_VOLUME) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (103, modeManager.isActiveMode (Modes.MODE_DEVICE_LAYER_PAN) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (104, PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (105, PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (106, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND5 : Modes.MODE_DEVICE_LAYER_SEND1) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (107, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND6 : Modes.MODE_DEVICE_LAYER_SEND2) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (108, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND7 : Modes.MODE_DEVICE_LAYER_SEND3) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (109, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_DEVICE_LAYER_SEND8 : Modes.MODE_DEVICE_LAYER_SEND4) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            return;
        }

        if (cd == null || !cd.hasLayers ())
        {
            this.disableSecondRow ();
            this.surface.updateButton (109, this.isPush2 ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH1_COLOR2_WHITE);
            return;
        }

        final int offset = getDrumPadIndex (cd);
        for (int i = 0; i < 8; i++)
        {
            final IChannel dl = cd.getLayerOrDrumPad (offset + i);
            int color = PushColors.PUSH1_COLOR_BLACK;
            if (dl.doesExist ())
            {
                if (muteState)
                {
                    if (!dl.isMute ())
                        color = PushColors.PUSH1_COLOR2_YELLOW_HI;
                }
                else
                    color = dl.isSolo () ? PushColors.PUSH1_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_GREY_LO;
            }
            this.surface.updateButton (102 + i, color);
        }
    }


    /**
     * Draw the fourth row.
     *
     * @param d The display
     * @param cd The cursor device
     */
    protected void drawRow4 (final Display d, final ICursorDevice cd)
    {
        // Drum Pad Bank has size of 16, layers only 8
        final int offset = getDrumPadIndex (cd);
        for (int i = 0; i < 8; i++)
        {
            final IChannel layer = cd.getLayerOrDrumPad (offset + i);
            final String n = StringUtils.shortenAndFixASCII (layer.getName (), layer.isSelected () ? 7 : 8);
            d.setCell (3, i, layer.isSelected () ? PushDisplay.RIGHT_ARROW + n : n);
        }
        d.allDone ();
    }


    protected static int getDrumPadIndex (final ICursorDevice cd)
    {
        if (cd.hasDrumPads ())
        {
            final IChannel selectedDrumPad = cd.getSelectedDrumPad ();
            if (selectedDrumPad != null && selectedDrumPad.getIndex () > 7)
                return 8;
        }
        return 0;
    }
}