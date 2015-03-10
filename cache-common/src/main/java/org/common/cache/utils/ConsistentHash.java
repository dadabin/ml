package org.common.cache.utils;

import java.util.ArrayList;
import java.util.List;

public class ConsistentHash<T,N> {
	
	//虚拟节点数，解决节点分布不均的问题
	private int replicas=64;
	//使用hash方法：md5，crc32
	
	Hasher hasher;
	//节点计数器
	private int targetCount=0;
	//位置对应节点，用于lookup中根据位置确定要访问的节点。
	private List<T> positionToTarget=new ArrayList<T>();
	//节点对应位置，用于删除节点
    private List<T> targetToPositions=new ArrayList<T>();
    //是否已排序
    private boolean positionToTargetSorted=false;
    //构造函数，确定使用hash方法和虚拟节点数，虚拟节点数越多，分布越均匀，但程序的分布式运算越慢
	
    public ConsistentHash(HasherE hasherE,int replicas){
    	this.hasher=hasher==null?(new Crc32Hasher()):hasher;
    	this.replicas=replicas;
    }
    
    
    //添加节点，根据虚拟节点数，将节点分布到多个虚拟位置上。
    private void addTarget(int target) throws Exception{
    	if(this.targetToPositions.get(target)==null){
    		throw new Exception("");
    	}
    	//hash the target into multiple positions
    	for(int i=0;i<replicas;i++){
    		hasher.hash(target+"");
    	}
    	
    	this.positionToTargetSorted=false;
    	targetCount++;
    }
    
    public void addTargets(int[] targets) throws Exception{
    	for(int target:targets){
    		this.addTarget(target);
    	}
    }
    
    public void removeTarget(int target){
    	
    }
    
    public void getAllTargets(){
    	
    }
    
    //Looks up the target for the given resource
    private void lookup(String source){
    	
    	
    }
    /**
     * Get a list of targets for the resource,
     * 
     * -
     * 查找当前的资源对应的节点
     * 节点为空则返回空，节点只有一个则返回该节点，
     * 对当前资源进行hash，对所有的位置进行排序，在有序的位置列上寻找当前资源的位置
     * 当全部没有找到的时候，将资源的位置确定为有序位置的第一个（形成一个环）
     * 返回所有找到的节点。
     * @param requestCount
     * @throws Exception 
     */
    private void lookupList(String resource,int requestedCount) throws Exception{
    	if(requestedCount==0){
    		throw new Exception("Invalid count requested");
    	}
    	//handle no targets
    	if(this.positionToTarget.size()==0){
    		//return null;
    		return;
    	}
    	//optimize single target
    	if(this.targetCount==1){
    		
    	}
    	List<T> results=new ArrayList<T>();
    	
    	
    	
    	//hash resouce to a position
    	long resoucePosition = this.hasher.hash(resource);
    	
    	//loop to start - search values below the resoucePosition
     	for(T value:this.positionToTarget){
     		if(results.contains(value)){
     			results.add(value);
     		}
     		//return when enough results, or list exhausted
     		if(results.size()==requestedCount||results.size()==this.targetCount){
     			// return results;
     		}
     	}
     	
     // return results after iterating through both "parts"
    	
    	
    	
    }
    
    /**
     * Sorts the internal mapping(positions to targets) by position
     */
    private void sortPositionTargets(){
    	//sort by key(position) if not already
    	if(this.positionToTargetSorted){
    		
    		this.positionToTargetSorted=true;
    	}
    }
    
    public void print(){
    	System.out.println("...");
    	
    }
    
    public interface Hasher{
    	public long hash(String val); 
    }
    
    class Md5Hasher implements Hasher{

		public long hash(String val) {
			return 0;
		}
    	
    }
    
   public class Crc32Hasher implements Hasher{

		public long hash(String val) {
			
			return CRC32.getCRC32(val);
			
		}
    }
    
    public enum HasherE{
		MD5,CRC32
	}
    
    

}
