package li.cil.oc.integration.thaumicenergistics

import appeng.api.parts.IPartHost
import li.cil.oc.api.driver
import li.cil.oc.api.driver.{EnvironmentProvider, NamedBlock}
import li.cil.oc.api.machine.{Arguments, Callback, Context}
import li.cil.oc.integration.ManagedTileEntityEnvironment
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import thaumicenergistics.api.ThEApi
import thaumicenergistics.common.parts.PartEssentiaImportBus

object DriverEssentiaImportBus extends driver.SidedBlock {
  override def worksWith(world: World, x: Int, y: Int, z: Int, side: ForgeDirection) =
    world.getTileEntity(x, y, z) match {
      case container: IPartHost => ForgeDirection.VALID_DIRECTIONS.map(container.getPart).filter(obj => { obj != null }).exists(_.isInstanceOf[PartEssentiaImportBus])
      case _ => false
    }

  override def createEnvironment(world: World, x: Int, y: Int, z: Int, side: ForgeDirection) = new Environment(world, world.getTileEntity(x, y, z).asInstanceOf[IPartHost])

  final class Environment(val world: World, val host: IPartHost) extends ManagedTileEntityEnvironment[IPartHost](host, "essentia_importbus") with NamedBlock with PartEnvironmentBase {
    override def preferredName = "essentia_importbus"

    override def priority = 2

    @Callback(doc = "function(side:number[, slot:number]):string -- Get the configuration of the import bus pointing in the specified direction.")
    def getImportConfiguration(context: Context, args: Arguments): Array[AnyRef] = getPartConfig[PartEssentiaImportBus](context, args)

    @Callback(doc = "function(side:number[, slot:number][, aspect:string]):boolean -- Configure the import bus pointing in the specified direction to import essentia matching the specified type.")
    def setImportConfiguration(context: Context, args: Arguments): Array[AnyRef] = setPartConfig[PartEssentiaImportBus](context, args)

    @Callback(doc = "function(side:number):number -- Get the number of valid slots in this import bus.")
    def getImportSlotSize(context: Context, args: Arguments): Array[AnyRef] = getSlotSize[PartEssentiaImportBus](context, args)

  }

  object Provider extends EnvironmentProvider {
    override def getEnvironment(stack: ItemStack): Class[_] =
      if (ThEApi.instance.parts.Essentia_ImportBus.getStack.isItemEqual(stack))
        classOf[Environment]
      else null
  }
}
