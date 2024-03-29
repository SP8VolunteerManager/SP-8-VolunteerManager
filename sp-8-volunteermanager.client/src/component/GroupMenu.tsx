// GroupMenu.tsx

interface Group {
  group_id: string;
  name: string;
  role: string;
}

interface GroupMenuProps {
  currentGroup: Group | null;
  onSelectGroup: (id: string, name: string, role: string) => void;
  groups: Group[];
  onCreateGroup: () => void;
  onJoinGroup: () => void;
}

const GroupMenu: React.FC<GroupMenuProps> = ({ currentGroup, groups, onSelectGroup, onCreateGroup, onJoinGroup }) => {
  return (
    <div>
      {groups.map(group => ( // Lists all the groups with handlers for button clicks
        <div 
          key={group.group_id} 
          onClick={() => onSelectGroup(group.group_id, group.name, group.role)} // Updates the selected group in Dashboard.tsx
          style={{
            cursor: 'pointer', 
            padding: '10px', 
            margin: '5px', 
            border: '1px solid #ccc', 
            borderRadius: '5px',
            textDecoration: group.group_id === currentGroup?.group_id ? 'underline' : 'none', // Underlines the currently selected group
          }}
        >
          {group.name}
        </div>
      ))}
      <div
        onClick={onCreateGroup} // Update the state in Dashboard if 'Create a group' is selected
        style={{
          cursor: 'pointer', 
          padding: '10px', 
          margin: '5px', 
          border: '1px solid #ccc', 
          borderRadius: '5px',
        }}
      >
        Create a Group
      </div>
      <div
        onClick={onJoinGroup}
        style={{
          cursor: 'pointer', 
          padding: '10px', 
          margin: '5px', 
          border: '1px solid #ccc', 
          borderRadius: '5px',
        }}
      >
        Join a Group
      </div>
    </div>
  );
};

export default GroupMenu;