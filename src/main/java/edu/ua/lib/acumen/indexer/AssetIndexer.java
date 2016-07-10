package edu.ua.lib.acumen.indexer;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

import edu.ua.lib.acumen.Statistics;
import edu.ua.lib.acumen.database.Asset;
import edu.ua.lib.acumen.database.AssetDAO;
import edu.ua.lib.acumen.repo.Location;

public class AssetIndexer {
	
	private File file;
	private String typeId, repoLoc;
	
	public AssetIndexer(File file, Map<String, String> info){
		this.file = file;
		this.typeId = info.get("type_id");
		//this.ext = info.get("ext");
		this.repoLoc = info.get("repo_loc");
		//System.out.print(file.getName()+" -- ASSET\n");
	}
	
	public void index(){
		AssetDAO aDAO = new AssetDAO();
		long id = -1;
		try {
			id = aDAO.exists(this.repoLoc);
			if (id >= 0){
				if (this.file.lastModified() != aDAO.lastModified(id)){
					Asset oldAsset = aDAO.get(this.repoLoc);
					Asset newAsset = new Asset(
							oldAsset.getId(),
							oldAsset.getAssetTypeId(),
							this.repoLoc,
							Location.fullURL(this.file.getAbsolutePath()),
							Location.fullURL(this.file.getParent())+File.separator,
							oldAsset.getFileId(),
							this.file.length(),
							this.file.lastModified(),
							oldAsset.getStatusTypeId(),
							1);
					aDAO.update(newAsset);
				}
				else{
					aDAO.found(id);
				}
			}
			else{
				Asset newAsset = new Asset(
						-1,
						Integer.parseInt(this.typeId),
						this.repoLoc,
						Location.fullURL(this.file.getAbsolutePath()),
						Location.fullURL(this.file.getParent())+File.separator,
						aDAO.getParentID(this.repoLoc),
						this.file.length(),
						this.file.lastModified(),
						1,
						1);
				aDAO.insert(newAsset);
				Statistics.addedAsset();
			}
		} catch (SQLException e) {
			Statistics.assetFailed(file.getName());
			e.printStackTrace();
		} finally {
			aDAO = null;
		}
	}
}