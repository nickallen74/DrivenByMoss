// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.resource.ChannelType;


/**
 * Default data for an empty track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyTrackData implements ITrack
{
    /** The singleton. */
    public static final ITrack INSTANCE = new EmptyTrackData ();


    /**
     * Constructor.
     */
    private EmptyTrackData ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public ChannelType getType ()
    {
        return ChannelType.UNKNOWN;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroup ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecArm ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldNotes ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldAudioData ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getCrossfadeMode ()
    {
        return "AB";
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return new double []
        {
            0.0,
            0.0,
            0.0
        };
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSends ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public ISend getSend (final int sendIndex)
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSlots ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSlot (final int slotIndex)
    {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (final int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final double value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (final int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final double value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setRecArm (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void makeVisible ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectAndMakeVisible ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfadeModeAsNumber (final int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeMode (final String mode)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfadeModeAsNumber ()
    {
        // Intentionally empty
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfadeModeAsNumber (final int modeValue)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCrossfadeMode ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ISlot [] getSelectedSlots ()
    {
        // Intentionally empty
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getSelectedSlot ()
    {
        // Intentionally empty
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getEmptySlot (final int startFrom)
    {
        // Intentionally empty
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollClipPageBackwards ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollClipPageForwards ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAtStartOfChain ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAtEndOfChain ()
    {
        // Intentionally empty
    }
}
