import { api as entityModule } from '../entity-module/GraphicEntityModule.js';

export class Renderer {
  static get moduleName() {
    return 'Renderer';
  }

  handleFrameData(frameInfo, frameData) {
    //console.log("Frame", frameInfo.number, "data:", frameData);

    if (frameData){
    this.lastTiles = frameData.tiles || [];
    }
    return {
      frameInfo,
      frameData,
    };
  }


  updateScene(previousData, currentData, progress) {
  if (currentData && currentData.frameData && currentData.frameData.tiles){
    // Total number of tiles to animate
    const tiles = currentData.frameData.tiles
    const totalTiles = tiles.length;

    // How many tiles should be visible at this frame based on progress (0 to 1)
    const tilesToShow = Math.floor(progress * totalTiles);
    for (let i = 0; i < tilesToShow; i++) {
        const tile = tiles[i];
        const entity = entityModule.entities.get(tile.id);
        if (entity) {
          entity.graphics.texture = PIXI.Texture.from(tile.texture);
        }
    }
  }
}

  reinitScene(){}

  animateScene (delta) {}

  handleGlobalData (players, globalData) {
      this.globalData = {
        players: players,
        playerCount: players.length
      }
    }

}
