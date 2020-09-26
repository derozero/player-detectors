package commoble.entitydetectors.blocks;

import commoble.entitydetectors.ResourceLocations;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.items.IItemHandler;

public class MobDetectorItemHandler implements IItemHandler
{
	public final MobDetectorTileEntity mobDetector;
	
	public static boolean isItemValidFilter(ItemStack stack)
	{
		return ItemTags.getCollection().get(ResourceLocations.SLIME_TAG).contains(stack.getItem());
	}
	
	public MobDetectorItemHandler(MobDetectorTileEntity mobDetector)
	{
		this.mobDetector = mobDetector;
	}

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.mobDetector.slimeStack;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		ItemStack existingStack = this.mobDetector.slimeStack;
		if (existingStack.getCount() > 0)	// already has item
		{
			return stack.copy();
		}
		else	// doesn't already have item
		{
			if (!simulate)
			{
				ItemStack newStackInInventory = stack.copy();
				newStackInInventory.setCount(1);
				this.mobDetector.slimeStack = newStackInInventory;
			}
			
			ItemStack remainder = stack.copy();
			remainder.shrink(1);
			return remainder;
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack outStack = this.mobDetector.slimeStack.copy();
		if (!simulate)
		{
			this.mobDetector.slimeStack = ItemStack.EMPTY;
		}
		return outStack;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return isItemValidFilter(stack);
	}

}
